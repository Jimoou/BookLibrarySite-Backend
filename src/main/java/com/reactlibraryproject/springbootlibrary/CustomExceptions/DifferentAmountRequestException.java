package com.reactlibraryproject.springbootlibrary.CustomExceptions;

public class DifferentAmountRequestException extends RuntimeException{
    public DifferentAmountRequestException() {
        super("결제 요청된 금액이 다릅니다.");
    }
}
