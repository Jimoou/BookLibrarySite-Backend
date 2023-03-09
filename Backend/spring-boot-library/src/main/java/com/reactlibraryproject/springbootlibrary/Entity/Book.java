package com.reactlibraryproject.springbootlibrary.Entity;


import javax.persistence.*;

import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "book")
@Data
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String author;

    private String description;

    private int copies;

    private int copiesAvailable;

    private String category;

    private String img;
}

