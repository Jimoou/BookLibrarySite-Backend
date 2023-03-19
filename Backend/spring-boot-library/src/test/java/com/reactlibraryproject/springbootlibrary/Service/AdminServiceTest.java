package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.DAO.BookRepository;
import com.reactlibraryproject.springbootlibrary.DAO.CartItemRepository;
import com.reactlibraryproject.springbootlibrary.DAO.CheckoutRepository;
import com.reactlibraryproject.springbootlibrary.DAO.ReviewRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Book;
import com.reactlibraryproject.springbootlibrary.RequestModels.AddBookRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("관리자 서비스 테스트")
class AdminServiceTest {
  @InjectMocks private AdminService adminService;

  public Book testBook;

  public Long bookId;

  @Mock private BookRepository bookRepository;

  @Mock private ReviewRepository reviewRepository;

  @Mock private CheckoutRepository checkoutRepository;

  @Mock private CartItemRepository cartItemRepository;

  @BeforeEach
  public void setUp() {
    bookId = 1L;
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
  }

  @Test
  @DisplayName("대여 가능한 책 수량 + 테스트")
  void increaseBookQuantity() throws Exception {
    // Given
    given(bookRepository.findById(bookId)).willReturn(Optional.of(testBook));

    // When
    adminService.increaseBookQuantity(bookId);

    // Then
    verify(bookRepository, times(1)).save(testBook);
    assertEquals(2, testBook.getCopiesAvailable());
    assertEquals(2, testBook.getCopies());
  }

  @Test
  @DisplayName("대여 가능한 책 수량 - 테스트")
  void decreaseBookQuantity() throws Exception {
    // Given
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));

    // When
    adminService.decreaseBookQuantity(bookId);

    // Then
    verify(bookRepository, times(1)).save(testBook);
    assertEquals(0, testBook.getCopiesAvailable());
    assertEquals(0, testBook.getCopies());
  }

  @Test
  @DisplayName("새로운 책 추가 테스트")
  void postBook() {
    // Given
    AddBookRequest addBookRequest =
        new AddBookRequest(
            "Title",
            "Author",
            "Description",
            1,
            "Category",
            "Image",
            "Publisher",
            10000,
            2,
            "2022-01-01");

    // When
    adminService.postBook(addBookRequest);

    // Then
    verify(bookRepository, times(1)).save(any(Book.class));
  }

  @Test
  @DisplayName("책 삭제 테스트")
  void deleteBook() throws Exception {
    // Given
    when(bookRepository.findById((bookId))).thenReturn(Optional.of(testBook));

    // When
    adminService.deleteBook(bookId);

    // Then
    verify(bookRepository, times(1)).delete(testBook);
    verify(checkoutRepository, times(1)).deleteAllByBookId(bookId);
    verify(reviewRepository, times(1)).deleteAllByBookId(bookId);
    verify(cartItemRepository, times(1)).deleteAllByBookId(bookId);
  }
}
