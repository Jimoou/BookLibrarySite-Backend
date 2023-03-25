package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.DAO.BookRepository;
import com.reactlibraryproject.springbootlibrary.DAO.CheckoutHistoryRepository;
import com.reactlibraryproject.springbootlibrary.DAO.CheckoutRepository;
import com.reactlibraryproject.springbootlibrary.DAO.CoinRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Book;
import com.reactlibraryproject.springbootlibrary.Entity.Checkout;
import com.reactlibraryproject.springbootlibrary.Entity.CheckoutHistory;
import com.reactlibraryproject.springbootlibrary.Entity.Coin;
import com.reactlibraryproject.springbootlibrary.ReponseModels.CurrentLoansResponse;
import com.reactlibraryproject.springbootlibrary.Utils.BookFinder;
import com.reactlibraryproject.springbootlibrary.Utils.ValidateCheckout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("책 관련 서비스 테스트")
class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    public Book testBook;

    public Checkout checkout;

    public Long bookId;

    public String userEmail;

    public Coin testCoin;
    @Mock
    private BookFinder bookFinder;
    @Mock
    private ValidateCheckout validateCheckout;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CheckoutRepository checkoutRepository;

    @Mock
    private CheckoutHistoryRepository checkoutHistoryRepository;

    @Mock
    private CoinRepository coinRepository;

    @Mock
    private CoinService coinService;

    @BeforeEach
    void setUp() {
        bookId = 1L;
        userEmail = "user@email.com";
        testBook =
         Book.builder()
          .id(1L)
          .title("Title")
          .author("Author")
          .description("Description")
          .copies(1)
          .copiesAvailable(1)
          .category("Category")
          .img("Image")
          .publisher("Publisher")
          .price(10000)
          .coin(0)
          .publicationDate("2023-03-18")
          .build();
        checkout =
         Checkout.builder()
          .id(1L)
          .userEmail(userEmail)
          .checkoutDate(LocalDate.now().toString())
          .returnedDate(LocalDate.now().plusDays(7).toString())
          .bookId(bookId)
          .build();
        testCoin = Coin.builder()
         .userEmail(userEmail)
         .amount(3)
         .build();
    }

    @Test
    @DisplayName("책 대여 테스트")
    void checkoutBook() {
        // Given
        given(bookFinder.bookFinder(bookId)).willReturn(testBook);
        given(coinRepository.findByUserEmail(userEmail)).willReturn(testCoin);
        doNothing()
         .when(validateCheckout)
         .validate(any(Book.class), any(String.class), any(Long.class));
        doNothing()
         .when(coinService)
         .useCoin(any(String.class), any(String.class), any(Integer.class), any(String.class));
        int coinsToUse = 3;

        // When
        Book checkedOutBook = bookService.checkoutBook(userEmail, bookId, coinsToUse);

        // Then
        assertNotNull(checkedOutBook);
        assertEquals(testBook.getId(), checkedOutBook.getId());
        assertEquals(testBook.getTitle(), checkedOutBook.getTitle());
    }


    @Test
    @DisplayName("책 대여 여부 테스트")
    void checkoutBookByUser() {
        // Given

        // When
        boolean validate = bookService.checkoutBookByUser(userEmail, bookId);

        // Then
        assertFalse(validate);
    }

    @Test
    @DisplayName("현재 대여중인 책의 수 테스트")
    void currentLoansCount() {
        // Given
        given(checkoutRepository.findBooksByUserEmail(userEmail))
         .willReturn(Collections.singletonList(checkout));

        // When
        int loansCount = bookService.currentLoansCount(userEmail);

        // Then
        assertEquals(1, loansCount);
    }

    @Test
    @DisplayName("현재 대여중인 책 목록 테스트")
    void currentLoans() {
        // Given
        List<Checkout> checkoutList = Collections.singletonList(checkout);
        given(checkoutRepository.findBooksByUserEmail(userEmail)).willReturn(checkoutList);
        given(bookRepository.findBooksByBookIds(Collections.singletonList(bookId)))
         .willReturn(Collections.singletonList(testBook));

        // When
        List<CurrentLoansResponse> currentLoansRespons =
         bookService.currentLoans(userEmail);

        // Then
        assertEquals(1, currentLoansRespons.size());
        List<Book> returnedBooks =
         currentLoansRespons.stream()
          .map(CurrentLoansResponse::getBook)
          .collect(Collectors.toList());
        assertEquals(Collections.singletonList(testBook), returnedBooks);
        List<Integer> returnedDaysLeft =
         currentLoansRespons.stream()
          .map(CurrentLoansResponse::getDaysLeft)
          .collect(Collectors.toList());
        assertEquals(Collections.singletonList(7), returnedDaysLeft);
    }

    @Test
    @DisplayName("책 반납 테스트")
    void returnBook() {
        // Given
        given(bookFinder.bookFinder(bookId)).willReturn(testBook);
        given(validateCheckout.validatedCheckout(userEmail, bookId)).willReturn(checkout);

        // When
        bookService.returnBook(userEmail, bookId);

        // Then
        assertEquals(2, testBook.getCopiesAvailable());
        verify(bookRepository).save(testBook);
        verify(checkoutRepository).deleteById(checkout.getId());
        verify(checkoutHistoryRepository).save(any(CheckoutHistory.class));
    }

    @Test
    @DisplayName("대출 기간 초기화 테스트")
    void renewLoan() {
        // Given
        LocalDate currentDate = LocalDate.now();
        given(validateCheckout.validatedCheckout(userEmail, bookId)).willReturn(checkout);

        // When
        bookService.renewLoan(userEmail, bookId);

        // Then
        assertEquals(currentDate.plusDays(7).toString(), checkout.getReturnedDate());
        verify(checkoutRepository).save(checkout);
    }
}
