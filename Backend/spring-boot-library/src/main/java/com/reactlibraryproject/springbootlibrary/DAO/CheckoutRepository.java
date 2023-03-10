package com.reactlibraryproject.springbootlibrary.DAO;

import com.reactlibraryproject.springbootlibrary.Entity.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CheckoutRepository extends JpaRepository<Checkout, Long> {

    Checkout findByUserEmailAndBookId(String userEmail, Long bookId);

    List<Checkout> findBooksByUserEmail(String userEmail);

    @Modifying
    @Query("DELETE FROM Checkout WHERE bookId in :book_id")
    void deleteAllByBookId(@Param("book_id") Long BookId);
}
