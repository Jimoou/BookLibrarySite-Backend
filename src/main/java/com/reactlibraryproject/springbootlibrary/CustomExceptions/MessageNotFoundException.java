package com.reactlibraryproject.springbootlibrary.CustomExceptions;

public class MessageNotFoundException extends RuntimeException{
    public MessageNotFoundException() {
        super("메세지를 찾을 수 없습니다.");
    }
}
