package com.reactlibraryproject.springbootlibrary.DAO;

import com.reactlibraryproject.springbootlibrary.Entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "payment-histories")
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {

  PaymentHistory findByUserEmail(String userEmail);

  List<PaymentHistory> findPaymentByUserEmail(String userEmail);

  List<PaymentHistory> findByUserEmailAndStatusIsNull(String userEmail);

  void deleteByUserEmailAndStatusIsNull(String userEmail);
}
