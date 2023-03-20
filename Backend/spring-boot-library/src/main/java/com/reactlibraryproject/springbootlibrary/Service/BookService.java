package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.DAO.BookRepository;
import com.reactlibraryproject.springbootlibrary.DAO.CheckoutRepository;
import com.reactlibraryproject.springbootlibrary.DAO.CheckoutHistoryRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Book;
import com.reactlibraryproject.springbootlibrary.Entity.Checkout;
import com.reactlibraryproject.springbootlibrary.Entity.CheckoutHistory;
import com.reactlibraryproject.springbootlibrary.ReponseModels.ShelfCurrentLoansResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class BookService {

  private final BookRepository bookRepository;

  private final CheckoutRepository checkoutRepository;

  private final CheckoutHistoryRepository checkoutHistoryRepository;

  public Book checkoutBook(String userEmail, Long bookId) throws Exception {
    Optional<Book> book = bookRepository.findById(bookId);

    Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);

    if (book.isEmpty() || validateCheckout != null || book.get().getCopiesAvailable() <= 0) {
      throw new Exception("Book doesn't exist or already checked out by user");
    }
    book.get().setCopiesAvailable(book.get().getCopiesAvailable() - 1);
    bookRepository.save(book.get());

    Checkout checkout =
        Checkout.builder()
            .userEmail(userEmail)
            .checkoutDate(LocalDate.now().toString())
            .returnedDate(LocalDate.now().plusDays(7).toString())
            .bookId(bookId)
            .build();

    checkoutRepository.save(checkout);

    return book.get();
  }

  public boolean checkoutBookByUser(String userEmail, Long bookId) {
    Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
    return validateCheckout != null;
  }

  public int currentLoansCount(String userEmail) {
    return checkoutRepository.findBooksByUserEmail(userEmail).size();
  }

  public List<ShelfCurrentLoansResponse> currentLoans(String userEmail) {
    List<Checkout> checkoutList = checkoutRepository.findBooksByUserEmail(userEmail);
    Map<Long, Checkout> checkoutMap = createCheckoutMap(checkoutList);
    List<Book> books = bookRepository.findBooksByBookIds(new ArrayList<>(checkoutMap.keySet()));

    return generateShelfCurrentLoansResponseList(checkoutMap, books);
  }

  private Map<Long, Checkout> createCheckoutMap(List<Checkout> checkoutList) {
    return checkoutList.stream()
        .collect(Collectors.toMap(Checkout::getBookId, Function.identity()));
  }

  private List<ShelfCurrentLoansResponse> generateShelfCurrentLoansResponseList(
      Map<Long, Checkout> checkoutMap, List<Book> books) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate currentDate = LocalDate.now();

    return books.stream()
        .filter(book -> checkoutMap.containsKey(book.getId()))
        .map(book -> generateShelfCurrentLoanResponse(book, checkoutMap, formatter, currentDate))
        .collect(Collectors.toList());
  }

  private ShelfCurrentLoansResponse generateShelfCurrentLoanResponse(
      Book book,
      Map<Long, Checkout> checkoutMap,
      DateTimeFormatter formatter,
      LocalDate currentDate) {
    Checkout checkout = checkoutMap.get(book.getId());
    LocalDate returnDate = LocalDate.parse(checkout.getReturnedDate(), formatter);
    long differenceInDays = ChronoUnit.DAYS.between(currentDate, returnDate);
    return new ShelfCurrentLoansResponse(book, (int) differenceInDays);
  }

  public void returnBook(String userEmail, Long bookId) throws Exception {
    Optional<Book> book = bookRepository.findById(bookId);
    Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);

    if (book.isEmpty() || validateCheckout == null) {
      throw new Exception("Book does not exist or not checked out by user");
    }

    book.get().setCopiesAvailable(book.get().getCopiesAvailable() + 1);

    bookRepository.save(book.get());
    checkoutRepository.deleteById(validateCheckout.getId());

    saveCheckoutHistory(validateCheckout, book.get());
  }

  private void saveCheckoutHistory(Checkout validateCheckout, Book book) {
    CheckoutHistory checkoutHistory =
        CheckoutHistory.builder()
            .userEmail(validateCheckout.getUserEmail())
            .checkoutDate(validateCheckout.getCheckoutDate())
            .returnedDate(LocalDate.now().toString())
            .title(book.getTitle())
            .author(book.getAuthor())
            .description(book.getDescription())
            .img(book.getImg())
            .build();

    checkoutHistoryRepository.save(checkoutHistory);
  }

  public void renewLoan(String userEmail, Long bookId) throws Exception {
    Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
    if (validateCheckout == null) {
      throw new Exception("Book is not checked out by user");
    }
    extendLoan(validateCheckout);
  }

  private void extendLoan(Checkout validateCheckout) {
    LocalDate currentDate = LocalDate.now();
    LocalDate returnDate = LocalDate.parse(validateCheckout.getReturnedDate());

    if (!returnDate.isBefore(currentDate)) {
      int renewalPeriod = 7;
      validateCheckout.setReturnedDate(currentDate.plusDays(renewalPeriod).toString());
      checkoutRepository.save(validateCheckout);
    }
  }
}
