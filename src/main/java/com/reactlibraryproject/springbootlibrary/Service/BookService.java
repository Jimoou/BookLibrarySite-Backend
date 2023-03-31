package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.CustomExceptions.InsufficientCoinsException;
import com.reactlibraryproject.springbootlibrary.DAO.BookRepository;
import com.reactlibraryproject.springbootlibrary.DAO.CheckoutRepository;
import com.reactlibraryproject.springbootlibrary.DAO.CheckoutHistoryRepository;
import com.reactlibraryproject.springbootlibrary.DAO.CoinRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Book;
import com.reactlibraryproject.springbootlibrary.Entity.Checkout;
import com.reactlibraryproject.springbootlibrary.Entity.CheckoutHistory;
import com.reactlibraryproject.springbootlibrary.Entity.Coin;
import com.reactlibraryproject.springbootlibrary.ReponseModels.CurrentLoansResponse;
import com.reactlibraryproject.springbootlibrary.Utils.BookFinder;
import com.reactlibraryproject.springbootlibrary.Utils.ValidateCheckout;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class BookService {

    private BookRepository bookRepository;

    private CheckoutRepository checkoutRepository;

    private CheckoutHistoryRepository checkoutHistoryRepository;

    private CoinService coinService;

    private BookFinder bookFinder;
    private ValidateCheckout validateCheckout;
    private final CoinRepository coinRepository;

    public Book checkoutBook(String userEmail, Long bookId, int coinsToUse) {
        // Exception
        Book book = bookFinder.bookFinder(bookId);

        Coin coin = coinRepository.findByUserEmail(userEmail);
        if (coin.getAmount() < coinsToUse) {
            throw new InsufficientCoinsException();
        }

        validateCheckout.validate(book, userEmail, bookId);

        // Function
        book.setCopiesAvailable(book.getCopiesAvailable() - 1);
        bookRepository.save(book);
        createCheckout(userEmail, bookId);
        LocalDate currentDate = LocalDate.now();
        coinService.useCoin(userEmail, book.getTitle(), coinsToUse, currentDate.toString());

        return book;
    }

    private void createCheckout(String userEmail, Long bookId) {
        Checkout checkout =
         Checkout.builder()
          .userEmail(userEmail)
          .checkoutDate(LocalDate.now().toString())
          .returnedDate(LocalDate.now().plusDays(7).toString())
          .bookId(bookId)
          .build();

        checkoutRepository.save(checkout);
    }

    public int currentLoansCount(String userEmail) {
        return checkoutRepository.findBooksByUserEmail(userEmail).size();
    }


    public List<CurrentLoansResponse> currentLoans(String userEmail) {
        List<Checkout> checkoutList = checkoutRepository.findBooksByUserEmail(userEmail);
        Map<Long, Checkout> checkoutMap = createCheckoutMap(checkoutList);
        List<Book> books = bookRepository.findBooksByBookIds(new ArrayList<>(checkoutMap.keySet()));
        LocalDate currentDate = LocalDate.now();

        return generateShelfCurrentLoansResponseList(checkoutMap, books, currentDate);
    }

    private Map<Long, Checkout> createCheckoutMap(List<Checkout> checkoutList) {
        return checkoutList.stream()
         .collect(Collectors.toMap(Checkout::getBookId, Function.identity()));
    }

    private List<CurrentLoansResponse> generateShelfCurrentLoansResponseList(
     Map<Long, Checkout> checkoutMap, List<Book> books, LocalDate currentDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return books.stream()
         .filter(book -> checkoutMap.containsKey(book.getId()))
         .map(book -> generateShelfCurrentLoanResponse(book, checkoutMap, formatter, currentDate))
         .collect(Collectors.toList());
    }

    private CurrentLoansResponse generateShelfCurrentLoanResponse(
     Book book,
     Map<Long, Checkout> checkoutMap,
     DateTimeFormatter formatter,
     LocalDate currentDate) {
        Checkout checkout = checkoutMap.get(book.getId());
        LocalDate returnDate = LocalDate.parse(checkout.getReturnedDate(), formatter);
        long differenceInDays = ChronoUnit.DAYS.between(currentDate, returnDate);

        return new CurrentLoansResponse(book, (int) differenceInDays);
    }

    public boolean checkoutBookByUser(String userEmail, Long bookId) {
        return validateCheckout.checkoutBookByUser(userEmail, bookId);
    }

    public void returnBook(String userEmail, Long bookId) {
        // Exception
        Book book = bookFinder.bookFinder(bookId);
        Checkout checkout = validateCheckout.validatedCheckout(userEmail, bookId);

        // Function
        book.setCopiesAvailable(book.getCopiesAvailable() + 1);

        bookRepository.save(book);
        checkoutRepository.deleteById(checkout.getId());

        saveCheckoutHistory(checkout, book);
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

    public void renewLoan(String userEmail, Long bookId) {
        // Exception
        Checkout checkout = validateCheckout.validatedCheckout(userEmail, bookId);

        // Function
        LocalDate currentDate = LocalDate.now();
        extendLoan(checkout, currentDate);
    }

    private void extendLoan(Checkout checkout, LocalDate currentDate) {
        LocalDate returnDate = LocalDate.parse(checkout.getReturnedDate());

        if (!returnDate.isBefore(currentDate)) {
            int renewalPeriod = 7;
            checkout.setReturnedDate(currentDate.plusDays(renewalPeriod).toString());
            checkoutRepository.save(checkout);
        }
    }
}
