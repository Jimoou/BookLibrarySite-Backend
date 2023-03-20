package com.reactlibraryproject.springbootlibrary.Utils;

import com.reactlibraryproject.springbootlibrary.CustomExceptions.BookNotFoundException;
import com.reactlibraryproject.springbootlibrary.DAO.BookRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Book;
import lombok.AllArgsConstructor;
import java.util.Optional;

@AllArgsConstructor
public class BookFinder {
    private BookRepository bookRepository;

    public Book bookFinder(Long bookId) {
        Optional<Book> book = bookRepository.findById((bookId));
        if (book.isEmpty()) {
            throw new BookNotFoundException();
        }
        return book.get();
    }
}
