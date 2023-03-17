package com.reactlibraryproject.springbootlibrary.ReponseModels;

import com.reactlibraryproject.springbootlibrary.Entity.Book;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrentCartItemResponse {
    private Book book;
    private int amount;
}
