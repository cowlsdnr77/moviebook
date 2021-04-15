package com.miniproject.moviebook.repository;

import com.miniproject.moviebook.model.Movie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {

//    Optional<List<Movie>> findByTitleLike(String title);

    Slice<Movie> findByTitleLike(String title, Pageable pageable);
}
