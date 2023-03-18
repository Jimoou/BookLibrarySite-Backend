package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.DAO.BookRepository;
import com.reactlibraryproject.springbootlibrary.DAO.CartItemRepository;
import com.reactlibraryproject.springbootlibrary.DAO.PurchaseHistoryRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Book;
import com.reactlibraryproject.springbootlibrary.Entity.CartItem;
import com.reactlibraryproject.springbootlibrary.Entity.PurchaseHistory;
import com.reactlibraryproject.springbootlibrary.ReponseModels.PurchaseHistoryResponse;
import com.reactlibraryproject.springbootlibrary.RequestModels.AddPurchaseRequest;
import com.reactlibraryproject.springbootlibrary.RequestModels.SuccessPurchaseRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class PurchaseService {

    private final PurchaseHistoryRepository purchaseHistoryRepository;
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;

    public List<PurchaseHistoryResponse> purchaseHistories(String userEmail) throws Exception {
        List<PurchaseHistory> purchaseHistory = purchaseHistoryRepository.findPurchaseByUserEmail(userEmail);

        if (purchaseHistory.isEmpty()) {
            throw new Exception("The user's payment history does not exist.");
        }

        Map<String, List<PurchaseHistory>> purchaseHistoryMap = purchaseHistory.stream()
         .collect(Collectors.groupingBy(PurchaseHistory::getPaymentKey));

        return purchaseHistoryMap.entrySet().stream()
         .map(entry -> {
             List<PurchaseHistory> purchaseHistories = entry.getValue();
             String orderName = purchaseHistories.get(0).getTitle();
             int totalPrice = purchaseHistories.stream().mapToInt(PurchaseHistory::getPrice).sum();
             String purchaseDate = purchaseHistories.get(0).getPurchaseDate();
             if (purchaseHistories.size() > 1) {
                 orderName = orderName + "외" + (purchaseHistories.size() - 1) + "건";
             }
             return new PurchaseHistoryResponse(entry.getKey(), orderName, totalPrice, purchaseDate);
         })
         .collect(Collectors.toList());
    }

    public void addPendingPurchases(String userEmail, List<AddPurchaseRequest> purchaseRequests) {
        purchaseRequests.forEach(purchaseRequest -> {
            try {
                Optional<Book> foundBook = bookRepository.findById(purchaseRequest.getBookId());
                if (foundBook.isEmpty()) {
                    throw new Exception("Book does not exist");
                }
                Book book = foundBook.get();
                PurchaseHistory newPurchase = PurchaseHistory.builder()
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
                purchaseHistoryRepository.save(newPurchase);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }




    public void deleteFailPurchase(String userEmail) {
        List<PurchaseHistory> nullHistory = purchaseHistoryRepository.findPurchaseByUserEmail(userEmail);
        for (PurchaseHistory nullValue : nullHistory) {
            if (nullValue.getStatus() == null) {
                purchaseHistoryRepository.delete(nullValue);
            }
        }
    }

    public void updateSuccessPurchase(String userEmail, SuccessPurchaseRequest successPurchaseRequest) {
        List<PurchaseHistory> pendingPurchases = purchaseHistoryRepository.findPurchaseByUserEmail(userEmail);

        pendingPurchases.stream()
         .filter(purchase -> purchase.getStatus() == null && successPurchaseRequest.getStatus() != null)
         .forEach(purchase -> {
             purchase.setPaymentKey(successPurchaseRequest.getPaymentKey());
             purchase.setOrderId(successPurchaseRequest.getOrderId());
             purchase.setPurchaseDate(successPurchaseRequest.getPurchaseDate());
             purchase.setStatus(successPurchaseRequest.getStatus());
             purchaseHistoryRepository.save(purchase);

             CartItem cartItem = cartItemRepository.findByUserEmailAndId(userEmail, purchase.getCartItemId());
             cartItemRepository.delete(cartItem);
         });
    }
}
