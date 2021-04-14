package com.miniproject.moviebook.service;

import com.miniproject.moviebook.dto.ReviewRequestDto;
import com.miniproject.moviebook.model.Movie;
import com.miniproject.moviebook.model.Review;
import com.miniproject.moviebook.model.User;
import com.miniproject.moviebook.repository.MovieRepository;
import com.miniproject.moviebook.repository.ReviewRepository;
import com.miniproject.moviebook.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                ()-> new IllegalArgumentException("movieError")
        );
        return reviewRepository.findByMovie(movie);
    }

    // 해당 영화 리뷰 작성
    public Review createReview(ReviewRequestDto requestDto, Long u_id, Long m_id) {
        User user = userRepository.findById(u_id).orElseThrow(
                () -> new IllegalArgumentException("user Error")
        );

        Movie movie = movieRepository.findById(m_id).orElseThrow(
                () -> new IllegalArgumentException("movie Error")
        );

        Review review = new Review(requestDto, user.getUsername(), movie);

        return reviewRepository.save(review);
    }
}
