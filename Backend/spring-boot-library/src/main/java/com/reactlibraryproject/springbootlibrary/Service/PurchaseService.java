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

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class PurchaseService {

    private final PurchaseHistoryRepository purchaseHistoryRepository;
    private final BookRepository bookRepository;

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
                 orderName = orderName + "외" + purchaseHistories.size() + "건";
             }
             return new PurchaseHistoryResponse(entry.getKey(), orderName, totalPrice, purchaseDate);
         })
         .collect(Collectors.toList());
    }
    public void addPurchaseHistory(String userEmail, List<AddPurchaseRequest> purchaseRequests) throws Exception {
        for(AddPurchaseRequest purchaseRequest : purchaseRequests) {
            Optional<Book> findedBook = bookRepository.findById(purchaseRequest.getBookId());
            if (findedBook.isEmpty()) {
                throw new Exception("Book does not exist");
            }
            PurchaseHistory saveHistory = PurchaseHistory.builder()
             .userEmail(userEmail)
             .title(findedBook.get().getTitle())
             .author(findedBook.get().getAuthor())
             .category(findedBook.get().getCategory())
             .img(findedBook.get().getImg())
             .publisher(findedBook.get().getPublisher())
             .amount(purchaseRequest.getAmount())
             .price(findedBook.get().getPrice() * purchaseRequest.getAmount())
             .build();
            purchaseHistoryRepository.save(saveHistory);
        }
    }

    public void deleteFailPurchase(String userEmail) {
        List<PurchaseHistory> nullHistory = purchaseHistoryRepository.findPurchaseByUserEmail(userEmail);
        for (PurchaseHistory nullValue : nullHistory) {
            if (nullValue.getStatus() == null) {
                purchaseHistoryRepository.delete(nullValue);
            }
        }
    }

    public void successPurchaseUpdate(String userEmail, SuccessPurchaseRequest successPurchaseRequest){
        System.out.println(successPurchaseRequest);
        List<PurchaseHistory> waitHistory = purchaseHistoryRepository.findPurchaseByUserEmail(userEmail);
        for (PurchaseHistory waitedHistory : waitHistory) {
            if (waitedHistory.getStatus() == null && successPurchaseRequest.getStatus() != null) {
                waitedHistory.setPaymentKey(successPurchaseRequest.getPaymentKey());
                waitedHistory.setOrderId(successPurchaseRequest.getOrderId());
                waitedHistory.setPurchaseDate(successPurchaseRequest.getPurchaseDate());
                waitedHistory.setStatus(successPurchaseRequest.getStatus());
                purchaseHistoryRepository.save(waitedHistory);
            }
        }
    }
}
