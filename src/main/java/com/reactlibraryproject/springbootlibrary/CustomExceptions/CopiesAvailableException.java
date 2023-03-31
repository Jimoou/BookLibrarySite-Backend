package com.reactlibraryproject.springbootlibrary.CustomExceptions;

public class CopiesAvailableException extends RuntimeException {
  public CopiesAvailableException() {
    super("대여 불가능한 책입니다. 대여 가능한 권 수가 없습니다.");
  }
}
