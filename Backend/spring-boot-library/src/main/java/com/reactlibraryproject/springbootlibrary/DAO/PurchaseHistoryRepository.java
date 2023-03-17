package com.reactlibraryproject.springbootlibrary.DAO;

import com.reactlibraryproject.springbootlibrary.Entity.PurchaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RepositoryRestResource(path = "purchase-history")
public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory, Long> {

    PurchaseHistory findByUserEmail(String userEmail);

    List<PurchaseHistory> findPurchaseByUserEmail(String userEmail);

    List<PurchaseHistory> findByUserEmailAndPaymentKey(
     @RequestParam("email") String userEmail
    ,@RequestParam("paymentKey") String paymentKey);
}
