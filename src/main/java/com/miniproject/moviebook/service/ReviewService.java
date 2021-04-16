package com.miniproject.moviebook.service;

import com.miniproject.moviebook.auth.PrincipalDetails;
import com.miniproject.moviebook.dto.ReviewRequestDto;
import com.miniproject.moviebook.model.Movie;
import com.miniproject.moviebook.model.Review;
import com.miniproject.moviebook.model.User;
import com.miniproject.moviebook.repository.MovieRepository;
import com.miniproject.moviebook.repository.ReviewRepository;
import com.miniproject.moviebook.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    // 해당 영화 리뷰 목록 조회
    public List<Review> getReviewList(Long m_id) {
        Movie movie = movieRepository.findById(m_id).orElseThrow(
                ()-> new IllegalArgumentException("영화 정보가 없습니다.")
        );
        return reviewRepository.findByMovie(movie);
    }

    // 해당 영화 리뷰 작성
    public String createReview(ReviewRequestDto requestDto, Long m_id) {
        // 현재 유저 정보
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = ((PrincipalDetails) authentication.getPrincipal()).getUser();

        Movie movie = movieRepository.findById(m_id).orElseThrow(
                () -> new IllegalArgumentException("영화 정보가 없습니다.")
        );

        Review review = new Review(requestDto, user.getName(), movie, user);
        reviewRepository.save(review);
        return "리뷰가 작성되었습니다.";
    }

    // 해당 영화 리뷰 수정
    @Transactional
    public String updateReview(ReviewRequestDto requestDto, Long r_id) {
        Review review = reviewRepository.findById(r_id).orElseThrow(
                () -> new IllegalArgumentException("리뷰 정보가 없습니다.")
        );

        // 현재 유저 정보
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = ((PrincipalDetails) authentication.getPrincipal()).getUser();

        if (user.getU_id().equals(review.getUser().getU_id())) {
            review.update(requestDto);
            return "리뷰가 수정되었습니다.";
        } else {
            throw new IllegalArgumentException("유저 정보가 일치하지 않습니다.");
        }
    }

    // 해당 영화 리뷰 삭제
    public String deleteReview(Long r_id) {
        Review review = reviewRepository.findById(r_id).orElseThrow(
                () -> new IllegalArgumentException("리뷰 정보가 없습니다.")
        );

        // 현재 유저 정보
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = ((PrincipalDetails) authentication.getPrincipal()).getUser();

        if (user.getU_id().equals(review.getUser().getU_id())) {
            reviewRepository.deleteById(review.getR_id());
            return "리뷰가 삭제되었습니다.";
        } else {
            throw new IllegalArgumentException("유저 정보가 일치하지 않습니다.");
        }
    }
}
