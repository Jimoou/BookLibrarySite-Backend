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

@Entity
@Table(name = "checkout_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutHistory {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    private String userEmail;

    private String checkoutDate;

    private String returnedDate;

    private String title;

    private String author;

    private String description;

    private String img;
}
