package com.miniproject.moviebook.repository;

import com.miniproject.moviebook.model.Movie;
import com.miniproject.moviebook.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByMovie(Movie movie);
}
