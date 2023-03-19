package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.DAO.BookRepository;
import com.reactlibraryproject.springbootlibrary.DAO.PurchaseHistoryRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Book;
import com.reactlibraryproject.springbootlibrary.Entity.PurchaseHistory;
import com.reactlibraryproject.springbootlibrary.ReponseModels.PurchaseHistoryResponse;
import com.reactlibraryproject.springbootlibrary.RequestModels.AddPurchaseRequest;
import com.reactlibraryproject.springbootlibrary.RequestModels.SuccessPurchaseRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class PurchaseService {

  private final PurchaseHistoryRepository purchaseHistoryRepository;
  private final BookRepository bookRepository;
  private final CartItemService cartItemService;

  public List<PurchaseHistoryResponse> purchaseHistories(String userEmail) throws Exception {
    List<PurchaseHistory> purchaseHistory =
        purchaseHistoryRepository.findPurchaseByUserEmail(userEmail);
    Map<String, List<PurchaseHistory>> purchaseHistoryMap =
        groupPurchaseHistoryByOrderId(purchaseHistory);

    return createPurchaseHistoryResponses(purchaseHistoryMap);
  }

  private Map<String, List<PurchaseHistory>> groupPurchaseHistoryByOrderId(
      List<PurchaseHistory> purchaseHistory) {
    return purchaseHistory.stream().collect(Collectors.groupingBy(PurchaseHistory::getOrderId));
  }

  private List<PurchaseHistoryResponse> createPurchaseHistoryResponses(
      Map<String, List<PurchaseHistory>> purchaseHistoryMap) {
    return purchaseHistoryMap.entrySet().stream()
        .map(this::createPurchaseHistoryResponse)
        .collect(Collectors.toList());
  }

  private PurchaseHistoryResponse createPurchaseHistoryResponse(
      Map.Entry<String, List<PurchaseHistory>> entry) {
    List<PurchaseHistory> purchaseHistories = entry.getValue();
    String orderName = getOrderName(purchaseHistories);
    int totalPrice = getTotalPrice(purchaseHistories);
    String purchaseDate = purchaseHistories.get(0).getPurchaseDate();

    return new PurchaseHistoryResponse(entry.getKey(), orderName, totalPrice, purchaseDate);
  }

  private String getOrderName(List<PurchaseHistory> purchaseHistories) {
    String orderName = purchaseHistories.get(0).getTitle();
    if (purchaseHistories.size() > 1) {
      orderName = orderName + "외" + (purchaseHistories.size() - 1) + "건";
    }
    return orderName;
  }

  private int getTotalPrice(List<PurchaseHistory> purchaseHistories) {
    return purchaseHistories.stream().mapToInt(PurchaseHistory::getPrice).sum();
  }

  public void addPendingPurchases(String userEmail, List<AddPurchaseRequest> purchaseRequests) {
    purchaseRequests.forEach(purchaseRequest -> processPurchaseRequest(userEmail, purchaseRequest));
  }

  private void processPurchaseRequest(String userEmail, AddPurchaseRequest purchaseRequest) {
    try {
      Optional<Book> book = bookRepository.findById(purchaseRequest.getBookId());
      PurchaseHistory newPurchase = createPurchaseHistory(userEmail, purchaseRequest, book.get());
      purchaseHistoryRepository.save(newPurchase);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private PurchaseHistory createPurchaseHistory(
      String userEmail, AddPurchaseRequest purchaseRequest, Book book) {
    return PurchaseHistory.builder()
        .userEmail(userEmail)
        .title(book.getTitle())
        .author(book.getAuthor())
        .category(book.getCategory())
        .img(book.getImg())
        .publisher(book.getPublisher())
        .amount(purchaseRequest.getAmount())
        .price(book.getPrice() * purchaseRequest.getAmount())
        .cartItemId(purchaseRequest.getCartItemId())
        .build();
  }

  public void deleteFailPurchase(String userEmail) {
    purchaseHistoryRepository.deleteByUserEmailAndStatusIsNull(userEmail);
  }

  public void updateSuccessPurchase(
      String userEmail, SuccessPurchaseRequest successPurchaseRequest) {
    List<PurchaseHistory> pendingPurchases =
        purchaseHistoryRepository.findByUserEmailAndStatusIsNull(userEmail);

    pendingPurchases.stream()
        .filter(purchase -> successPurchaseRequest.getStatus() != null)
        .forEach(
            purchase ->
                updatePurchaseAndDeleteCartItem(userEmail, successPurchaseRequest, purchase));
  }

  private void updatePurchaseAndDeleteCartItem(
      String userEmail, SuccessPurchaseRequest successPurchaseRequest, PurchaseHistory purchase) {
    updatePurchase(purchase, successPurchaseRequest);
    cartItemService.deleteCartItem(userEmail, purchase.getCartItemId());
  }

  private void updatePurchase(
      PurchaseHistory purchase, SuccessPurchaseRequest successPurchaseRequest) {
    purchase.setPaymentKey(successPurchaseRequest.getPaymentKey());
    purchase.setOrderId(successPurchaseRequest.getOrderId());
    purchase.setPurchaseDate(successPurchaseRequest.getPurchaseDate());
    purchase.setStatus(successPurchaseRequest.getStatus());
    purchaseHistoryRepository.save(purchase);
  }
}
