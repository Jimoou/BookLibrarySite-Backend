package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.CustomExceptions.CopiesAvailableException;
import com.reactlibraryproject.springbootlibrary.DAO.BookRepository;
import com.reactlibraryproject.springbootlibrary.DAO.CartItemRepository;
import com.reactlibraryproject.springbootlibrary.DAO.CheckoutRepository;
import com.reactlibraryproject.springbootlibrary.DAO.ReviewRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Book;
import com.reactlibraryproject.springbootlibrary.RequestModels.AddBookRequest;
import com.reactlibraryproject.springbootlibrary.Utils.BookFinder;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class AdminService {

  private BookRepository bookRepository;
  private BookFinder bookFinder;
  private ReviewRepository reviewRepository;
  private CheckoutRepository checkoutRepository;
  private CartItemRepository cartItemRepository;

  public void increaseBookQuantity(Long bookId) {
    // Exception
    Book book = bookFinder.bookFinder(bookId);

    // Function
    book.setCopiesAvailable(book.getCopiesAvailable() + 1);
    book.setCopies(book.getCopies() + 1);

    bookRepository.save(book);
  }

  public void decreaseBookQuantity(Long bookId) {
    // Exception
    Book book = bookFinder.bookFinder(bookId);

    if (book.getCopies() <= 0 || book.getCopiesAvailable() <= 0) {
      throw new CopiesAvailableException();
    }

    // Function
    book.setCopiesAvailable(book.getCopiesAvailable() - 1);
    book.setCopies(book.getCopies() - 1);

    bookRepository.save(book);
  }

  public void postBook(AddBookRequest addBookRequest) {
    Book book =
        Book.builder()
            .title(addBookRequest.getTitle())
            .author(addBookRequest.getAuthor())
            .description(addBookRequest.getDescription())
            .copies(addBookRequest.getCopies())
            .copiesAvailable(addBookRequest.getCopies())
            .category(addBookRequest.getCategory())
            .img(addBookRequest.getImg())
            .publisher(addBookRequest.getPublisher())
            .price(addBookRequest.getPrice())
            .coin(addBookRequest.getCoin())
            .publicationDate(addBookRequest.getPublicationDate())
            .build();

    bookRepository.save(book);
  }

  public void deleteBook(Long bookId) {
    // Exception
    Book book = bookFinder.bookFinder(bookId);

    // Function
    try {
      bookRepository.delete(book);
      checkoutRepository.deleteAllByBookId(bookId);
      reviewRepository.deleteAllByBookId(bookId);
      cartItemRepository.deleteAllByBookId(bookId);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
