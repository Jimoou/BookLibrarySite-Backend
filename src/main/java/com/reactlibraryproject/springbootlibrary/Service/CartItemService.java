package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.DAO.BookRepository;
import com.reactlibraryproject.springbootlibrary.DAO.CartItemRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Book;
import com.reactlibraryproject.springbootlibrary.Entity.CartItem;
import com.reactlibraryproject.springbootlibrary.ReponseModels.CurrentCartItemResponse;
import com.reactlibraryproject.springbootlibrary.Utils.CartItemFinder;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class CartItemService {
    private CartItemRepository cartItemRepository;
    private BookRepository bookRepository;
    private CartItemFinder cartItemFinder;


    public void addBookInCart(String userEmail, Long bookId) {
        CartItem cartItem = cartItemRepository.findByUserEmailAndBookId(userEmail, bookId);
        int amount = cartItem == null ? 1 : cartItem.getAmount() + 1;

        if (cartItem == null) {
            cartItem = CartItem.builder().userEmail(userEmail).bookId(bookId).build();
        }

        cartItem.setAmount(amount);
        cartItemRepository.save(cartItem);
    }

    public void deleteCartItem(String userEmail, Long id) {
        // Exception
        CartItem cartItem = cartItemFinder.itemFinder(userEmail, id);

        // Function
        cartItemRepository.delete(cartItem);
    }

    public List<CurrentCartItemResponse> currentCart(String userEmail) {
        List<CartItem> cartItemList = cartItemRepository.findBooksByUserEmail(userEmail);
        Map<Long, CartItem> cartItemMap = createCartItemMap(cartItemList);

        List<Book> booksInCart =
         bookRepository.findBooksByBookIds(new ArrayList<>(cartItemMap.keySet()));

        return createCurrentCartItemResponseList(cartItemMap, booksInCart);
    }

    private Map<Long, CartItem> createCartItemMap(List<CartItem> cartItemList) {
        return cartItemList.stream()
         .collect(Collectors.toMap(CartItem::getBookId, Function.identity()));
    }

    private List<CurrentCartItemResponse> createCurrentCartItemResponseList(Map<Long, CartItem> cartItemMap, List<Book> booksInCart) {
        return booksInCart.stream()
         .map(book -> createCurrentCartItemResponse(cartItemMap, book))
         .collect(Collectors.toList());
    }

    private CurrentCartItemResponse createCurrentCartItemResponse(Map<Long, CartItem> cartItemMap, Book book) {
        CartItem cartItem = cartItemMap.get(book.getId());
        return new CurrentCartItemResponse(book, cartItem.getAmount(), cartItem.getId());
    }

    public void increaseAmount(String userEmail, Long id) {
        // Exception
        CartItem cartItem = cartItemFinder.itemFinder(userEmail, id);

        // Function
        cartItem.setAmount(cartItem.getAmount() + 1);
        cartItemRepository.save(cartItem);
    }

    public void decreaseAmount(String userEmail, Long id) {
        // Exception
        CartItem cartItem = cartItemFinder.itemFinder(userEmail, id);

        // Function
        cartItem.setAmount(Math.max(1, cartItem.getAmount() - 1));
        cartItemRepository.save(cartItem);
    }
}
