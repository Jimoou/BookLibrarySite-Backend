package com.reactlibraryproject.springbootlibrary.CustomExceptions;

public class InsufficientCoinsException extends RuntimeException{
    public InsufficientCoinsException() {
        super("코인이 부족합니다.");
    }
}
