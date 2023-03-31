package com.reactlibraryproject.springbootlibrary.Controller;

import com.reactlibraryproject.springbootlibrary.RequestModels.ReviewRequest;
import com.reactlibraryproject.springbootlibrary.Service.ReviewService;
import com.reactlibraryproject.springbootlibrary.Utils.ExtractJWT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin("https://springboot-library-add4e.web.app/")
@RestController
@AllArgsConstructor
@RequestMapping("/api/reviews")
@Tag(name="리뷰", description = "리뷰 API")
public class ReviewController {

    private ReviewService reviewService;

    @Operation(summary = "리뷰 검증", description = "유저가 이미 리뷰를 남긴 책인지를 검증해서 한번만 남길 수 있게함.")
    @GetMapping("/secure/user/book")
    public Boolean reviewBookByUser(@RequestHeader(value="Authorization") String token,
                                    @RequestParam Long bookId) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");

        if (userEmail == null) {
            throw new Exception("User email is missing");
        }
        return reviewService.reviewBookByUser(userEmail, bookId);

    }

    @Operation(summary = "리뷰 남기기")
    @PostMapping("/secure")
    public void postReview(@RequestHeader(value="Authorization") String token,
                           @RequestBody ReviewRequest reviewRequest) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        if (userEmail == null) {
            throw new Exception("User email is missing");
        }
        reviewService.postReview(userEmail, reviewRequest);
    }
}
