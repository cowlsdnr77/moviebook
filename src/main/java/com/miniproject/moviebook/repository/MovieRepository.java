package com.miniproject.moviebook.repository;

import com.miniproject.moviebook.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> findByTitle(String title);

    Optional<List<Movie>> findByTitleLike(String title);
}
