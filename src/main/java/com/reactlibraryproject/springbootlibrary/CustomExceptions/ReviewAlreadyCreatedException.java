package com.reactlibraryproject.springbootlibrary.CustomExceptions;

public class ReviewAlreadyCreatedException extends RuntimeException{
    public ReviewAlreadyCreatedException() {
        super("이미 리뷰를 작성한 책입니다.");
    }
}
