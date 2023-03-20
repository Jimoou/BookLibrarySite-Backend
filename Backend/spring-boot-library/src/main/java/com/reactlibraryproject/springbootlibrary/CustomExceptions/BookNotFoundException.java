package com.reactlibraryproject.springbootlibrary.CustomExceptions;

public class BookNotFoundException extends RuntimeException{

    public BookNotFoundException() {
        super("존재하지 않는 책입니다.");
    }
}
