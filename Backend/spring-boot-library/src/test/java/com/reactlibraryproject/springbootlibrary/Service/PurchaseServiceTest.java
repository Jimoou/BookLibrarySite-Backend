package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.DAO.BookRepository;
import com.reactlibraryproject.springbootlibrary.DAO.PurchaseHistoryRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Book;
import com.reactlibraryproject.springbootlibrary.Entity.PurchaseHistory;
import com.reactlibraryproject.springbootlibrary.ReponseModels.PurchaseHistoryResponse;
import com.reactlibraryproject.springbootlibrary.RequestModels.AddPurchaseRequest;
import com.reactlibraryproject.springbootlibrary.RequestModels.SuccessPurchaseRequest;
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
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("결제 테스트")
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class PurchaseServiceTest {

  @InjectMocks PurchaseService purchaseService;
  public Book testBook;
  public List<PurchaseHistory> purchaseHistoryList;
  public PurchaseHistory purchaseHistory;
  public Long bookId;
  public String userEmail;
  public AddPurchaseRequest purchaseRequest;

  @Mock private PurchaseHistoryRepository purchaseHistoryRepository;
  @Mock private CartItemService cartItemService;
  @Mock private BookRepository bookRepository;

  @BeforeEach
  void setUp() {
    bookId = 1L;
    userEmail = "user@email.com";
    testBook =
        Book.builder()
            .id(bookId)
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
    purchaseHistory =
        PurchaseHistory.builder()
            .id(1L)
            .userEmail(userEmail)
            .title(testBook.getTitle())
            .author(testBook.getAuthor())
            .category(testBook.getCategory())
            .img(testBook.getImg())
            .publisher(testBook.getPublisher())
            .amount(300)
            .price(testBook.getPrice())
            .cartItemId(1L)
            .build();

    purchaseHistoryList = Collections.singletonList(purchaseHistory);
    purchaseRequest = new AddPurchaseRequest(1L, 2, 1L);
  }

  @Test
  @DisplayName("결제 내역 - 승인이 완료된")
  void purchaseHistories() {
    // Given
    when(purchaseHistoryRepository.findPurchaseByUserEmail(userEmail))
        .thenReturn(purchaseHistoryList);

    // When
    List<PurchaseHistoryResponse> purchaseHistoryResponses =
        purchaseService.purchaseHistories(userEmail);

    // Then
    assertFalse(purchaseHistoryResponses.isEmpty());
    assertEquals(1, purchaseHistoryResponses.size());
    verify(purchaseHistoryRepository).findPurchaseByUserEmail(userEmail);
  }

  @Test
  @DisplayName("결제 내역 추가 - 결제 승인 전 테스트")
  void addPendingPurchases() {
    // Given
    when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
    when(purchaseHistoryRepository.save(any(PurchaseHistory.class))).thenReturn(null);

    // when
    purchaseService.addPendingPurchases(userEmail, Collections.singletonList(purchaseRequest));

    // then
    verify(bookRepository).findById(1L);
    verify(purchaseHistoryRepository).save(any(PurchaseHistory.class));
  }

  @Test
  @DisplayName("결제 취소 반환시 결제 내역 삭제")
  void deleteFailPurchase() {
    // Given

    // When
    purchaseService.deleteFailPurchase(userEmail);

    // Then
    verify(purchaseHistoryRepository, times(1)).deleteByUserEmailAndStatusIsNull(userEmail);
  }

  @Test
  @DisplayName("결제 내역 업데이트 - 결제 승인 후 테스트")
  void updateSuccessPurchase()throws Exception {
    // Given
    SuccessPurchaseRequest successPurchaseRequest =
        new SuccessPurchaseRequest("test_payment_key", "test_order_id", "2023-03-20", "success", 300);
    when(purchaseHistoryRepository.findByUserEmailAndStatusIsNull(userEmail))
        .thenReturn(purchaseHistoryList);
    // When
    purchaseService.updateSuccessPurchase(userEmail, successPurchaseRequest);

    // Then
    verify(purchaseHistoryRepository, times(1)).findByUserEmailAndStatusIsNull(userEmail);
    verify(purchaseHistoryRepository, times(1)).save(any(PurchaseHistory.class));
    verify(cartItemService, times(1))
        .deleteCartItem(userEmail, purchaseHistoryList.get(0).getCartItemId());
  }
}
