package com.reactlibraryproject.springbootlibrary.DAO;

import com.reactlibraryproject.springbootlibrary.Entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
@RepositoryRestResource(path = "cart-item")
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByUserEmailAndBookId(String userEmail, Long bookId);

    List<CartItem> findBooksByUserEmail(String userEmail);

    @Modifying
    @Query("DELETE FROM CartItem WHERE bookId in :book_id")
    void deleteAllByBookId(@Param("book_id") Long BookId);
}
