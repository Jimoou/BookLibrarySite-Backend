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
@Table(name="coin_using_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoinUsingHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @Column(name="user_email")
    private String userEmail;

    @Column(name="amount")
    private int amount;

    @Column(name="balance")
    private int balance;

    @Column(name = "checkout_date")
    private String checkoutDate;

    @Column(name = "book_title")
    private String bookTitle;

}
