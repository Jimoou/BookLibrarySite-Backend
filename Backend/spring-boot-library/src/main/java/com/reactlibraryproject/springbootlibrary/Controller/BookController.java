package com.reactlibraryproject.springbootlibrary.Controller;

import com.reactlibraryproject.springbootlibrary.Entity.Book;
import com.reactlibraryproject.springbootlibrary.ReponseModels.BooksInCartResponse;
import com.reactlibraryproject.springbootlibrary.ReponseModels.ShelfCurrentLoansResponse;
import com.reactlibraryproject.springbootlibrary.Service.BookService;
import com.reactlibraryproject.springbootlibrary.Utils.ExtractJWT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("https://localhost:3000")
@RestController
@AllArgsConstructor
@RequestMapping("/api/books")
@Tag(name="책", description="책 API")
public class BookController {

    private BookService bookService;

    @Operation(summary = "유저의 현재 대여중인 책 목록")
    @GetMapping("/secure/currentloans")
    public List<ShelfCurrentLoansResponse> currentLoans(@RequestHeader(value = "Authorization") String token) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        return bookService.currentLoans(userEmail);
    }

    @Operation(summary = "유저가 현재 대여중인 책의 수")
    @GetMapping("/secure/currentloans/count")
    public int currentLoansCount(@RequestHeader(value = "Authorization") String token) {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        return bookService.currentLoansCount(userEmail);
    }

    @Operation(summary= "유저가 현재 대여중인 책인지에 대한 검증", description = "이 API를 이용해 이미 대여중인 책을 다시 대여할 수 없게끔 제한한다.")
    @GetMapping("/secure/ischeckedout/byuser")
    public boolean checkoutBookByUser(@RequestHeader(value = "Authorization") String token, @RequestParam Long bookId) {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        return bookService.checkoutBookByUser(userEmail, bookId);
    }

    @Operation(summary = "책 대여")
    @PutMapping("/secure/checkout")
    public Book checkoutBook(@RequestHeader(value = "Authorization") String token, @RequestParam Long bookId) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        return bookService.checkoutBook(userEmail, bookId);
    }

    @Operation(summary = "책 반납")
    @PutMapping("/secure/return")
    public void returnBook(@RequestHeader(value = "Authorization") String token, @RequestParam Long bookId) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        bookService.returnBook(userEmail, bookId);
    }

    @Operation(summary = "대여 기간 연장")
    @PutMapping("/secure/renew/loan")
    public void renewLoan(@RequestHeader(value = "Authorization") String token, @RequestParam Long bookId) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        bookService.renewLoan(userEmail, bookId);
    }

    @Operation(summary = "유저의 장바구니 목록")
    @GetMapping("/secure/cart")
    public List<BooksInCartResponse> currentCart(@RequestHeader(value = "Authorization") String token) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        return bookService.currentCart(userEmail);
    }

    @Operation(summary = "장바구니에 책 추가")
    @PutMapping("/secure/cart/add/book")
    public void addBookInCart(@RequestHeader(value = "Authorization") String token,
                         @RequestParam Long bookId) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        bookService.addBookInCart(userEmail, bookId);
    }

    @Operation(summary = "장바구니에서 책 삭제")
    @PutMapping("/secure/cart/delete/book")
    public void deleteBookInCart(@RequestHeader(value = "Authorization") String token,
                              @RequestParam Long bookId) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        bookService.deleteBookInCart(userEmail, bookId);
    }

    @Operation(summary = "장바구니에서 책 수량 +")
    @PutMapping("/secure/cart/increase/book/amount")
    public void increaseAmount(@RequestHeader(value = "Authorization") String token,
                              @RequestParam Long bookId) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        bookService.increaseAmount(userEmail, bookId);
    }

    @Operation(summary = "장바구니에서 책 수량 -")
    @PutMapping("/secure/cart/decrease/book/amount")
    public void decreaseAmount(@RequestHeader(value = "Authorization") String token,
                                 @RequestParam Long bookId) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        bookService.decreaseAmount(userEmail, bookId);
    }
}
