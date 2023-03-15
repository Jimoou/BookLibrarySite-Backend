package com.reactlibraryproject.springbootlibrary.DAO;

import com.reactlibraryproject.springbootlibrary.Entity.CheckoutHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;

@RepositoryRestResource(path = "checkout-histories")
public interface CheckoutHistoryRepository extends JpaRepository<CheckoutHistory, Long> {

    Page<CheckoutHistory> findBooksByUserEmail(@RequestParam("email") String userEmail, Pageable pageable);
}
