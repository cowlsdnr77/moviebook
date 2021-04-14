package com.miniproject.moviebook.controller;

import com.miniproject.moviebook.dto.ReviewRequestDto;
import com.miniproject.moviebook.model.Review;
import com.miniproject.moviebook.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    //해당 영화 리뷰 목록 조회
    @GetMapping("/api/movies/reviews/{m_id}")
    public List<Review> getReviewList(@PathVariable Long m_id) {
        return reviewService.getReviewList(m_id);
    }

    //해당 영화 리뷰 작성
    @PostMapping("/api/movies/reviews/{u_id}/{m_id}")
    public Review createReview(@PathVariable Long u_id, @PathVariable Long m_id, @RequestBody  ReviewRequestDto requestDto) {
        return reviewService.createReview(requestDto, u_id, m_id);
    }
}
