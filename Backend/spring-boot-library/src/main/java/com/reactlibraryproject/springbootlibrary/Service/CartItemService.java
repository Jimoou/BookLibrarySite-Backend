package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.DAO.BookRepository;
import com.reactlibraryproject.springbootlibrary.DAO.CartItemRepository;
import com.reactlibraryproject.springbootlibrary.DAO.CheckoutHistoryRepository;
import com.reactlibraryproject.springbootlibrary.DAO.CheckoutRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Book;
import com.reactlibraryproject.springbootlibrary.Entity.CartItem;
import com.reactlibraryproject.springbootlibrary.ReponseModels.CurrentCartItemResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Transactional
public class CartItemService {
  private CartItemRepository cartItemRepository;
  private final BookRepository bookRepository;

  public void addBookInCart(String userEmail, Long bookId) throws Exception {
    CartItem cartItem = cartItemRepository.findByUserEmailAndBookId(userEmail, bookId);
    int amount = cartItem == null ? 1 : cartItem.getAmount() + 1;

    if (cartItem == null) {
      cartItem = CartItem.builder().userEmail(userEmail).bookId(bookId).build();
    }

    cartItem.setAmount(amount);
    cartItemRepository.save(cartItem);
  }

  public void deleteBookInCart(String userEmail, Long bookId) throws Exception {
    CartItem validateCartItem = cartItemRepository.findByUserEmailAndBookId(userEmail, bookId);

    if (validateCartItem == null) {
      throw new Exception("Book does not exist or not added in cart by user");
    }
    cartItemRepository.deleteById(validateCartItem.getId());
  }

  public List<CurrentCartItemResponse> currentCart(String userEmail) throws Exception {
    List<CurrentCartItemResponse> currentCartItemResponse = new ArrayList<>();

    List<CartItem> cartItemList = cartItemRepository.findBooksByUserEmail(userEmail);

    Map<Long, CartItem> purchaseMap = new HashMap<>();
    for (CartItem cartItem : cartItemList) {
      purchaseMap.put(cartItem.getBookId(), cartItem);
    }

    List<Book> booksInCart =
        bookRepository.findBooksByBookIds(new ArrayList<>(purchaseMap.keySet()));

    for (Book book : booksInCart) {
      CartItem cartItem = purchaseMap.get(book.getId());

      if (cartItem != null) {
        currentCartItemResponse.add(
            new CurrentCartItemResponse(book, cartItem.getAmount(), cartItem.getId()));
      }
    }
    return currentCartItemResponse;
  }

  public void increaseAmount(String userEmail, Long bookId) throws Exception {
    CartItem cartItem = cartItemRepository.findByUserEmailAndBookId(userEmail, bookId);

    if (cartItem == null) {
      throw new Exception("Book is not in the shopping cart.");
    }
    int amount = cartItem.getAmount() + 1;

    cartItem.setAmount(amount);
    cartItemRepository.save(cartItem);
  }

  public void decreaseAmount(String userEmail, Long bookId) throws Exception {
    CartItem cartItem = cartItemRepository.findByUserEmailAndBookId(userEmail, bookId);

    if (cartItem == null) {
      throw new Exception("Book is not in the shopping cart.");
    }
    int amount = 1;

    if (cartItem.getAmount() > 1) {
      amount = cartItem.getAmount() - 1;
    }

    cartItem.setAmount(amount);
    cartItemRepository.save(cartItem);
  }
}
