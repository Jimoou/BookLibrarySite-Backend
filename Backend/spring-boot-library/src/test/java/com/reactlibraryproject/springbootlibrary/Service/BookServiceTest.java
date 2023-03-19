package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.DAO.BookRepository;
import com.reactlibraryproject.springbootlibrary.DAO.CheckoutHistoryRepository;
import com.reactlibraryproject.springbootlibrary.DAO.CheckoutRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Book;
import com.reactlibraryproject.springbootlibrary.Entity.Checkout;
import com.reactlibraryproject.springbootlibrary.Entity.CheckoutHistory;
import com.reactlibraryproject.springbootlibrary.ReponseModels.ShelfCurrentLoansResponse;
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
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("책 관련 서비스 테스트")
class BookServiceTest {

  @InjectMocks private BookService bookService;

  public Book testBook;

  public Checkout checkout;

  public Long bookId;

  public String userEmail;

  @Mock private BookRepository bookRepository;

  @Mock private CheckoutRepository checkoutRepository;

  @Mock private CheckoutHistoryRepository checkoutHistoryRepository;

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
  }

  @Test
  @DisplayName("책 대여 테스트")
  void checkoutBook() throws Exception {
    // Given
    given(bookRepository.findById(bookId)).willReturn(Optional.of(testBook));
    given(checkoutRepository.findByUserEmailAndBookId(userEmail, bookId)).willReturn(null);

    // When
    Book checkedOutBook = bookService.checkoutBook(userEmail, bookId);

    // Then
    verify(checkoutRepository, times(1)).save(any(Checkout.class));
    assertNotNull(checkedOutBook);
  }

  @Test
  @DisplayName("책 대여 여부 테스트")
  void checkoutBookByUser() throws Exception {
    // Given
    given(checkoutRepository.findByUserEmailAndBookId(userEmail, bookId)).willReturn(null);

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
  void currentLoans() throws Exception {
    // Given
    List<Checkout> checkoutList = Collections.singletonList(checkout);
    given(checkoutRepository.findBooksByUserEmail(userEmail)).willReturn(checkoutList);
    given(bookRepository.findBooksByBookIds(Collections.singletonList(bookId)))
        .willReturn(Collections.singletonList(testBook));

    // When
    List<ShelfCurrentLoansResponse> shelfCurrentLoansResponses =
        bookService.currentLoans(userEmail);

    // Then
    assertEquals(1, shelfCurrentLoansResponses.size());
    List<Book> returnedBooks =
        shelfCurrentLoansResponses.stream()
            .map(ShelfCurrentLoansResponse::getBook)
            .collect(Collectors.toList());
    assertEquals(Collections.singletonList(testBook), returnedBooks);
    List<Integer> returnedDaysLeft =
        shelfCurrentLoansResponses.stream()
            .map(ShelfCurrentLoansResponse::getDaysLeft)
            .collect(Collectors.toList());
    assertEquals(Collections.singletonList(7), returnedDaysLeft);
  }

  @Test
  @DisplayName("책 반납 테스트")
  void returnBook() throws Exception {
    // Given
    given(bookRepository.findById(bookId)).willReturn(Optional.of(testBook));
    given(checkoutRepository.findByUserEmailAndBookId(userEmail, bookId)).willReturn(checkout);

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
  void renewLoan() throws Exception {
    // Given
    LocalDate currentDate = LocalDate.now();
    given(checkoutRepository.findByUserEmailAndBookId(userEmail, bookId)).willReturn(checkout);

    // When
    bookService.renewLoan(userEmail, bookId);

    // Then
    assertEquals(currentDate.plusDays(7).toString(), checkout.getReturnedDate());
    verify(checkoutRepository).save(checkout);
  }
}