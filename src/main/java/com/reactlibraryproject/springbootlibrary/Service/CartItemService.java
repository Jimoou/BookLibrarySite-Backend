package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.CustomExceptions.CartItemNotFoundException;import com.reactlibraryproject.springbootlibrary.DAO.BookRepository;
import com.reactlibraryproject.springbootlibrary.DAO.CartItemRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Book;
import com.reactlibraryproject.springbootlibrary.Entity.CartItem;
import com.reactlibraryproject.springbootlibrary.ReponseModels.CurrentCartItemResponse;
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
  private final BookRepository bookRepository;

  public void addBookInCart(String userEmail, Long bookId) {
    CartItem cartItem = cartItemRepository.findByUserEmailAndBookId(userEmail, bookId);
    int amount = cartItem == null ? 1 : cartItem.getAmount() + 1;

    if (cartItem == null) {
      cartItem = CartItem.builder().userEmail(userEmail).bookId(bookId).build();
    }

    cartItem.setAmount(amount);
    cartItemRepository.save(cartItem);
  }

  public void deleteCartItem(String userEmail, Long cartItemId) {
    CartItem cartItem = cartItemRepository.findByUserEmailAndId(userEmail, cartItemId);
    cartItemRepository.delete(cartItem);
  }

  public List<CurrentCartItemResponse> currentCart(String userEmail) {
    List<CartItem> cartItemList = cartItemRepository.findBooksByUserEmail(userEmail);
    Map<Long, CartItem> cartItemMap = createCartItemMap(cartItemList);

    List<Book> booksInCart =
        bookRepository.findBooksByBookIds(new ArrayList<>(cartItemMap.keySet()));

    return generateCurrentCartItemResponseList(cartItemMap, booksInCart);
  }
  private Map<Long, CartItem> createCartItemMap(List<CartItem> cartItemList) {
    return cartItemList.stream()
        .collect(Collectors.toMap(CartItem::getBookId, Function.identity()));
  }
  private List<CurrentCartItemResponse> generateCurrentCartItemResponseList(Map<Long, CartItem> purchaseMap, List<Book> booksInCart) {
    return booksInCart.stream()
        .map(
            book ->
                new CurrentCartItemResponse(
                    book,
                    purchaseMap.get(book.getId()).getAmount(),
                    purchaseMap.get(book.getId()).getId()))
        .collect(Collectors.toList());
  }
  public void increaseAmount(String userEmail, Long id)throws CartItemNotFoundException{
    CartItem cartItem = cartItemRepository.findByUserEmailAndId(userEmail, id);
    if (cartItem == null) {
      throw new CartItemNotFoundException();
    }

    cartItem.setAmount(cartItem.getAmount() + 1);
    cartItemRepository.save(cartItem);
  }

  public void decreaseAmount(String userEmail, Long id) {
    CartItem cartItem = cartItemRepository.findByUserEmailAndId(userEmail, id);

    if (cartItem == null) {
      throw new CartItemNotFoundException();
    }

    cartItem.setAmount(Math.max(1, cartItem.getAmount() - 1));
    cartItemRepository.save(cartItem);
  }
}
