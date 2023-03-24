package com.reactlibraryproject.springbootlibrary.DAO;

import com.reactlibraryproject.springbootlibrary.Entity.CoinChargingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "coin-charging-histories")
public interface CoinChargingHistoryRepository extends JpaRepository<CoinChargingHistory, Long> {
    List<CoinChargingHistory> findByUserEmail(String userEmail);
}