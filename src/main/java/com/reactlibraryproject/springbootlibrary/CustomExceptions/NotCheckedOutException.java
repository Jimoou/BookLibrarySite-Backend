package com.reactlibraryproject.springbootlibrary.CustomExceptions;

public class NotCheckedOutException extends RuntimeException {
  public NotCheckedOutException() {
    super("유저가 대여하지 않은 책입니다.");
  }
}
