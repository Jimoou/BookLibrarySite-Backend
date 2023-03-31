package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.DAO.ReviewRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Review;
import com.reactlibraryproject.springbootlibrary.RequestModels.ReviewRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("리뷰 서비스 테스트")
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;
    public Review review;
    public ReviewRequest reviewRequest;
    @Mock
    private ReviewRepository reviewRepository;

    @BeforeEach
    void setUp() {
        review = Review.builder()
         .bookId(1L)
         .rating(4)
         .userEmail("user@email.com")
         .reviewDescription("review")
         .date(Date.valueOf(LocalDate.now()))
         .build();
        reviewRequest = new ReviewRequest(4, 1L, "review");
    }

    @Test
    @DisplayName("리뷰 작성 테스트")
    void postReview() {
        //Given
        when(reviewRepository.findByUserEmailAndBookId("user@email.com", 1L)).thenReturn(null);

        //When
        reviewService.postReview("user@email.com", reviewRequest);

        //Then
        verify(reviewRepository).save(review);
    }

    @Test
    @DisplayName("리뷰 검증 테스트")
    void reviewBookByUser() {
        //Given
        when(reviewRepository.findByUserEmailAndBookId("user@email.com", 1L)).thenReturn(null);

        //When


        //Then
        assertFalse(reviewService.reviewBookByUser("user@email.com", 1L));
    }
}