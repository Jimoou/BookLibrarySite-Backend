package com.reactlibraryproject.springbootlibrary.CustomExceptions;

public class CartItemNotFoundException extends RuntimeException {

  public CartItemNotFoundException() {
    super("장바구니에 없습니다.");
  }
}
