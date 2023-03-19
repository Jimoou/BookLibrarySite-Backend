package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.DAO.BookRepository;
import com.reactlibraryproject.springbootlibrary.DAO.CartItemRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Book;
import com.reactlibraryproject.springbootlibrary.Entity.CartItem;
import com.reactlibraryproject.springbootlibrary.ReponseModels.CurrentCartItemResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("장바구니 테스트")
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class CartItemServiceTest {

  @InjectMocks private CartItemService cartItemService;

  public Long bookId;

  public String userEmail;

  public CartItem cartItem;

  public Book testBook;

  @Mock private CartItemRepository cartItemRepository;

  @Mock private BookRepository bookRepository;

  @BeforeEach
  void setUp() {
    bookId = 1L;
    userEmail = "user@email.com";
    cartItem = CartItem.builder().id(1L).userEmail(userEmail).amount(1).bookId(1L).build();
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
  @DisplayName("장바구니에 추가 테스트")
  void addBookInCart() throws Exception {
    // Given
    given(cartItemRepository.findByUserEmailAndBookId(userEmail, bookId)).willReturn(null);

    // When
    cartItemService.addBookInCart(userEmail, bookId);

    // Then
    verify(cartItemRepository).findByUserEmailAndBookId(userEmail, bookId);
    verify(cartItemRepository).save(any(CartItem.class));
  }

  @Test
  @DisplayName("장바구니에서 삭제 테스트")
  void deleteBookInCart() throws Exception {
    // Given
    given(cartItemRepository.findByUserEmailAndBookId(userEmail, bookId)).willReturn(cartItem);

    // When
    cartItemService.deleteBookInCart(userEmail, bookId);

    // Then
    verify(cartItemRepository).deleteById(bookId);
  }

  @Test
  void currentCart() throws Exception {
    // Given
    List<CartItem> cartItemList = Collections.singletonList(cartItem);
    List<Book> booksInCart = Collections.singletonList(testBook);
    given(cartItemRepository.findBooksByUserEmail(userEmail)).willReturn(cartItemList);
    given(bookRepository.findBooksByBookIds(Collections.singletonList(bookId))).willReturn(booksInCart);

    // When
    List<CurrentCartItemResponse> result = cartItemService.currentCart(userEmail);

    // Then
    assertEquals(1, result.size());
    assertEquals(testBook, result.get(0).getBook());
    assertEquals(cartItem.getAmount(), result.get(0).getAmount());
    assertEquals(cartItem.getId(), result.get(0).getCartItemId());
  }

  @Test
  @DisplayName("장바구니 수량 테스트")
  void increaseAmount()throws Exception {
    //Given
    given(cartItemRepository.findByUserEmailAndBookId(userEmail, bookId)).willReturn(cartItem);

    //When
    cartItemService.increaseAmount(userEmail, bookId);

    //Then
    assertEquals(cartItem.getAmount(), 2);
  }

  @Test
  @DisplayName("장바구니 수량 테스트")
  void decreaseAmount()throws Exception {
    //Given
    given(cartItemRepository.findByUserEmailAndBookId(userEmail, bookId)).willReturn(cartItem);

    //When
    cartItemService.decreaseAmount(userEmail, bookId);

    //Then
    assertEquals(cartItem.getAmount(), 1);
  }
}
