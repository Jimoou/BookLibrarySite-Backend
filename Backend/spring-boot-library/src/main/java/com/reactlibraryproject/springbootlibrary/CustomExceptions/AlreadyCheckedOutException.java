package com.reactlibraryproject.springbootlibrary.CustomExceptions;

public class AlreadyCheckedOutException extends RuntimeException {
  public AlreadyCheckedOutException() {
    super("이미 대여한 책입니다.");
  }
}
