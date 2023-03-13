package com.reactlibraryproject.springbootlibrary.DAO;

import com.reactlibraryproject.springbootlibrary.Entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    Purchase findByUserEmailAndBookId(String userEmail, Long bookId);

    List<Purchase> findBooksByUserEmail(String userEmail);

    @Modifying
    @Query("DELETE FROM Purchase WHERE bookId in :book_id")
    void deleteAllByBookId(@Param("book_id") Long BookId);
}
