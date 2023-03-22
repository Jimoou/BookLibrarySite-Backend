package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.CustomExceptions.ReviewAlreadyCreatedException;
import com.reactlibraryproject.springbootlibrary.DAO.ReviewRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Review;
import com.reactlibraryproject.springbootlibrary.RequestModels.ReviewRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;

@Service
@Transactional
@AllArgsConstructor
public class ReviewService {
    private ReviewRepository reviewRepository;

    public void postReview(String userEmail, ReviewRequest reviewRequest){
        Review validateReview = reviewRepository.findByUserEmailAndBookId(userEmail, reviewRequest.getBookId());
        if (validateReview != null) {
            throw new ReviewAlreadyCreatedException();
        }
        String reviewDescription = reviewRequest.getReviewDescription();

        Review review = Review.builder()
         .bookId(reviewRequest.getBookId())
         .rating(reviewRequest.getRating())
         .userEmail(userEmail)
         .reviewDescription(reviewDescription)
         .date(Date.valueOf(LocalDate.now()))
         .build();

        reviewRepository.save(review);
    }

    public boolean reviewBookByUser(String userEmail, Long bookId) {
        Review validateReview = reviewRepository.findByUserEmailAndBookId(userEmail, bookId);
        return validateReview != null;
    }
}
