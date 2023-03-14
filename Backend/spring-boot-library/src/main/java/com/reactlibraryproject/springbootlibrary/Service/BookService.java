package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.DAO.BookRepository;
import com.reactlibraryproject.springbootlibrary.DAO.CheckoutRepository;
import com.reactlibraryproject.springbootlibrary.DAO.HistoryRepository;
import com.reactlibraryproject.springbootlibrary.DAO.PurchaseRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Book;
import com.reactlibraryproject.springbootlibrary.Entity.Checkout;
import com.reactlibraryproject.springbootlibrary.Entity.History;
import com.reactlibraryproject.springbootlibrary.Entity.Purchase;
import com.reactlibraryproject.springbootlibrary.ReponseModels.BooksInCartResponse;
import com.reactlibraryproject.springbootlibrary.ReponseModels.ShelfCurrentLoansResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@AllArgsConstructor
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    private final CheckoutRepository checkoutRepository;

    private final HistoryRepository historyRepository;

    private final PurchaseRepository purchaseRepository;

    public Book checkoutBook(String userEmail, Long bookId) throws Exception {
        Optional<Book> book = bookRepository.findById(bookId);

        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);

        if (book.isEmpty() || validateCheckout != null || book.get().getCopiesAvailable() <= 0) {
            throw new Exception("Book doesn't exist or already checked out by user");
        }
        book.get().setCopiesAvailable(book.get().getCopiesAvailable() - 1);
        bookRepository.save(book.get());

        Checkout checkout = Checkout.builder()
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

    public List<ShelfCurrentLoansResponse> currentLoans(String userEmail) throws Exception {
        List<ShelfCurrentLoansResponse> shelfCurrentLoansResponses = new ArrayList<>();

        List<Checkout> checkoutList = checkoutRepository.findBooksByUserEmail(userEmail);

        Map<Long, Checkout> checkoutMap = new HashMap<>();
        for (Checkout checkout : checkoutList) {
            checkoutMap.put(checkout.getBookId(), checkout);
        }

        List<Book> books = bookRepository.findBooksByBookIds(new ArrayList<>(checkoutMap.keySet()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate currentDate = LocalDate.now();

        for (Book book : books) {
            Checkout checkout = checkoutMap.get(book.getId());

            if (checkout != null) {
                LocalDate returnDate = LocalDate.parse(checkout.getReturnedDate(), formatter);
                long differenceInDays = ChronoUnit.DAYS.between(currentDate, returnDate);

                shelfCurrentLoansResponses.add(new ShelfCurrentLoansResponse(book, (int) differenceInDays));
            }
        }
        return shelfCurrentLoansResponses;
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

        History history = History.builder()
         .userEmail(validateCheckout.getUserEmail())
         .checkoutDate(validateCheckout.getCheckoutDate())
         .returnedDate(LocalDate.now().toString())
         .title(book.get().getTitle())
         .author(book.get().getAuthor())
         .description(book.get().getDescription())
         .img(book.get().getImg())
         .build();

        historyRepository.save(history);
    }

    public void renewLoan(String userEmail, Long bookId) throws Exception {
        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);

        if (validateCheckout == null) {
            throw new Exception("Book does not exist or not checked out by user");
        }

        LocalDate currentDate = LocalDate.now();
        LocalDate returnDate = LocalDate.parse(validateCheckout.getReturnedDate());

        if (!returnDate.isBefore(currentDate)) {
            int renewalPeriod = 7;
            validateCheckout.setReturnedDate(currentDate.plusDays(renewalPeriod).toString());
            checkoutRepository.save(validateCheckout);
        }
    }

    public void addBookInCart(String userEmail, Long bookId) throws Exception {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new Exception("It was wrong bookId"));

        Purchase purchase = purchaseRepository.findByUserEmailAndBookId(userEmail, bookId);
        int amount = purchase == null ? 1 : purchase.getAmount() + 1;

        if (purchase == null) {
            purchase = Purchase.builder().userEmail(userEmail).bookId(bookId).build();
        }

        purchase.setAmount(amount);
        purchaseRepository.save(purchase);
    }


    public void deleteBookInCart(String userEmail, Long bookId) throws Exception {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new Exception("It was wrong bookId"));
        Purchase validatePurchase = purchaseRepository.findByUserEmailAndBookId(userEmail, bookId);

        if (validatePurchase == null) {
            throw new Exception("Book does not exist or not added in cart by user");
        }
        purchaseRepository.deleteById(validatePurchase.getId());
    }

    public List<BooksInCartResponse> currentCart(String userEmail) throws Exception {
        List<BooksInCartResponse> booksInCartResponses = new ArrayList<>();

        List<Purchase> purchaseList = purchaseRepository.findBooksByUserEmail(userEmail);

        Map<Long, Purchase> purchaseMap = new HashMap<>();
        for (Purchase purchase : purchaseList) {
            purchaseMap.put(purchase.getBookId(), purchase);
        }

        List<Book> booksInCart = bookRepository.findBooksByBookIds(new ArrayList<>(purchaseMap.keySet()));

        for (Book book : booksInCart) {
            Purchase purchase = purchaseMap.get(book.getId());

            if (purchase != null) {
                booksInCartResponses.add(new BooksInCartResponse(book, purchase.getAmount()));
            }
        }
        return booksInCartResponses;
    }

    public void increaseAmount(String userEmail, Long bookId) throws Exception {
        Purchase purchase = purchaseRepository.findByUserEmailAndBookId(userEmail, bookId);

        if (purchase == null) {
            throw new Exception("Book is not in the shopping cart.");
        }
        int amount = purchase.getAmount() + 1;

        purchase.setAmount(amount);
        purchaseRepository.save(purchase);

    }

    public void decreaseAmount(String userEmail, Long bookId) throws Exception {
        Purchase purchase = purchaseRepository.findByUserEmailAndBookId(userEmail, bookId);

        if (purchase == null) {
            throw new Exception("Book is not in the shopping cart.");
        }
        int amount = 1;

        if (purchase.getAmount() > 1) {
            amount = purchase.getAmount() - 1;
        }

        purchase.setAmount(amount);
        purchaseRepository.save(purchase);
    }
}
