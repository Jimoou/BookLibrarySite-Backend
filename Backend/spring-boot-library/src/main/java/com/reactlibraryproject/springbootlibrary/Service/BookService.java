package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.DAO.BookRepository;
import com.reactlibraryproject.springbootlibrary.DAO.CheckoutRepository;
import com.reactlibraryproject.springbootlibrary.DAO.CheckoutHistoryRepository;
import com.reactlibraryproject.springbootlibrary.DAO.CartItemRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Book;
import com.reactlibraryproject.springbootlibrary.Entity.Checkout;
import com.reactlibraryproject.springbootlibrary.Entity.CheckoutHistory;
import com.reactlibraryproject.springbootlibrary.Entity.CartItem;
import com.reactlibraryproject.springbootlibrary.ReponseModels.CurrentCartItemResponse;
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

    private final CheckoutHistoryRepository checkoutHistoryRepository;

    private final CartItemRepository cartItemRepository;

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

        CheckoutHistory checkoutHistory = CheckoutHistory.builder()
         .userEmail(validateCheckout.getUserEmail())
         .checkoutDate(validateCheckout.getCheckoutDate())
         .returnedDate(LocalDate.now().toString())
         .title(book.get().getTitle())
         .author(book.get().getAuthor())
         .description(book.get().getDescription())
         .img(book.get().getImg())
         .build();

        checkoutHistoryRepository.save(checkoutHistory);
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

        CartItem cartItem = cartItemRepository.findByUserEmailAndBookId(userEmail, bookId);
        int amount = cartItem == null ? 1 : cartItem.getAmount() + 1;

        if (cartItem == null) {
            cartItem = CartItem.builder().userEmail(userEmail).bookId(bookId).build();
        }

        cartItem.setAmount(amount);
        cartItemRepository.save(cartItem);
    }


    public void deleteBookInCart(String userEmail, Long bookId) throws Exception {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new Exception("It was wrong bookId"));
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

        List<Book> booksInCart = bookRepository.findBooksByBookIds(new ArrayList<>(purchaseMap.keySet()));

        for (Book book : booksInCart) {
            CartItem cartItem = purchaseMap.get(book.getId());

            if (cartItem != null) {
                currentCartItemResponse.add(new CurrentCartItemResponse(book, cartItem.getAmount()));
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
