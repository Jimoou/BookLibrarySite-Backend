package com.reactlibraryproject.springbootlibrary.RequestModels;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddBookRequest {

    private String title;

    private String author;

    private String description;

    private int copies;

    private String category;

    private String img;

    private String publisher;

    private int price;

    private int coin;

    private String publicationDate;

}
