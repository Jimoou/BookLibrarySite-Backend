package com.reactlibraryproject.springbootlibrary.DAO;

import com.reactlibraryproject.springbootlibrary.Entity.CoinUsingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "coin-using-histories")
public interface CoinUsingHistoryRepository extends JpaRepository<CoinUsingHistory, Long> {
    List<CoinUsingHistory> findByUserEmail(String userEmail);
}
