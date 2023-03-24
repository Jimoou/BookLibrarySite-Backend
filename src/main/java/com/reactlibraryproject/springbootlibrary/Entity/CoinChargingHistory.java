package com.reactlibraryproject.springbootlibrary.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;

@Entity
@Table(name="coin_charging_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoinChargingHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @Column(name="user_email")
    private String userEmail;

    @Column(name="amount")
    private int amount;

    @Column(name = "price")
    private int price;

    @Column(name = "payment_key")
    private String paymentKey;

    @Column(name = "payment_date")
    private String paymentDate;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "status")
    private String status;

}
