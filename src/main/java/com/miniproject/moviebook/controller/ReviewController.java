package com.miniproject.moviebook.controller;

import com.miniproject.moviebook.dto.ReviewRequestDto;
import com.miniproject.moviebook.model.Review;
import com.miniproject.moviebook.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    //해당 영화 리뷰 목록 조회
    @GetMapping("/api/movies/reviews/list/{m_id}")
    public Page<Review> getReviewList(@PathVariable Long m_id, @RequestParam(value = "page") int page) {
        return reviewService.getReviewList(m_id, page);
    }

    //해당 영화 리뷰 작성
    @PostMapping("/api/movies/reviews/authentication/{m_id}")
    public String createReview(@PathVariable Long m_id, @RequestBody  ReviewRequestDto requestDto) {
        return reviewService.createReview(requestDto, m_id);
    }

    //해당 영화 리뷰 수정
    @PutMapping("/api/movies/reviews/authentication/{r_id}")
    public String updateReview(@PathVariable Long r_id, @RequestBody ReviewRequestDto requestDto) {
        return reviewService.updateReview(requestDto, r_id);
    }

    //해당 영화 리뷰 삭제
    @DeleteMapping("/api/movies/reviews/authentication/{r_id}")
    public String deleteReview(@PathVariable Long r_id) {
        return reviewService.deleteReview(r_id);
    }

    // 예외 처리
    @ExceptionHandler({IllegalArgumentException.class})
    public Map<String, String> handleException(Exception e) {
        Map<String, String> map = new HashMap<>();
        map.put("errMsg", e.getMessage());
        return map;
    }
}
