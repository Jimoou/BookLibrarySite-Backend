package com.reactlibraryproject.springbootlibrary.Utils;

import com.reactlibraryproject.springbootlibrary.CustomExceptions.AlreadyCheckedOutException;
import com.reactlibraryproject.springbootlibrary.CustomExceptions.CopiesAvailableException;
import com.reactlibraryproject.springbootlibrary.CustomExceptions.NotCheckedOutException;
import com.reactlibraryproject.springbootlibrary.DAO.CheckoutRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Book;
import com.reactlibraryproject.springbootlibrary.Entity.Checkout;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ValidateCheckout {
  private CheckoutRepository checkoutRepository;

  public void validate(Book book, String userEmail, Long bookId) {
    if (checkoutBookByUser(userEmail, bookId)) {
      throw new AlreadyCheckedOutException();
    } else if (!checkoutAvailable(book)) {
      throw new CopiesAvailableException();
    }
  }

  public Checkout validatedCheckout(String userEmail, Long bookId) {
    if (checkoutBookByUser(userEmail, bookId)) {
      return checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
    } else {
      throw new NotCheckedOutException();
    }
  }

  public boolean checkoutBookByUser(String userEmail, Long bookId) {
    Checkout checkout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
    return checkout != null;
  }

  public boolean checkoutAvailable(Book book) {
    return book.getCopies() > 0 && book.getCopiesAvailable() > 0;
  }
}
