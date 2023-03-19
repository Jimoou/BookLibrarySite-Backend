package com.reactlibraryproject.springbootlibrary.Controller;

import com.reactlibraryproject.springbootlibrary.ReponseModels.CurrentCartItemResponse;
import com.reactlibraryproject.springbootlibrary.Service.CartItemService;
import com.reactlibraryproject.springbootlibrary.Utils.ExtractJWT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin("https://localhost:3000")
@RestController
@AllArgsConstructor
@RequestMapping("/api/cart")
@Tag(name="장바구니", description="장바구니 관련 API")
public class CartItemController {

    private CartItemService cartItemService;

    @Operation(summary = "유저의 장바구니 목록")
    @GetMapping("/secure")
    public List<CurrentCartItemResponse> currentCart(@RequestHeader(value = "Authorization") String token) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        return cartItemService.currentCart(userEmail);
    }

    @Operation(summary = "장바구니에 책 추가")
    @PutMapping("/secure/add/book")
    public void addBookInCart(@RequestHeader(value = "Authorization") String token,
                              @RequestParam Long bookId) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        cartItemService.addBookInCart(userEmail, bookId);
    }

    @Operation(summary = "장바구니에서 책 삭제")
    @PutMapping("/secure/delete/book")
    public void deleteBookInCart(@RequestHeader(value = "Authorization") String token,
                                 @RequestParam Long bookId) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        cartItemService.deleteBookInCart(userEmail, bookId);
    }

    @Operation(summary = "장바구니에서 책 수량 +")
    @PutMapping("/secure/increase/book/amount")
    public void increaseAmount(@RequestHeader(value = "Authorization") String token,
                               @RequestParam Long bookId) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        cartItemService.increaseAmount(userEmail, bookId);
    }

    @Operation(summary = "장바구니에서 책 수량 -")
    @PutMapping("/secure/decrease/book/amount")
    public void decreaseAmount(@RequestHeader(value = "Authorization") String token,
                               @RequestParam Long bookId) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        cartItemService.decreaseAmount(userEmail, bookId);
    }
}
