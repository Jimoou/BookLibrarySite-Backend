package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.DAO.BookRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Book;
import com.reactlibraryproject.springbootlibrary.RequestModels.AddBookRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class AdminService {

    private BookRepository bookRepository;

    public void postBook(AddBookRequest addBookRequest) {
        Book book = Book.builder()
         .title(addBookRequest.getTitle())
         .author(addBookRequest.getAuthor())
         .description(addBookRequest.getDescription())
         .copies(addBookRequest.getCopies())
         .copiesAvailable(addBookRequest.getCopies())
         .category(addBookRequest.getCategory())
         .img(addBookRequest.getImg())
         .build();

        bookRepository.save(book);
    }

}
