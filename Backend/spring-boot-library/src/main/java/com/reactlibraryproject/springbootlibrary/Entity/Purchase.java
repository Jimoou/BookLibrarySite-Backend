package com.reactlibraryproject.springbootlibrary.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "purchase")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String userEmail;

    private int amount;

    private Long bookId;
}
