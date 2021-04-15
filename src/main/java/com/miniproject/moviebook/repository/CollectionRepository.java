package com.miniproject.moviebook.repository;

import com.miniproject.moviebook.model.Collection;
import com.miniproject.moviebook.model.Movie;
import com.miniproject.moviebook.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollectionRepository extends JpaRepository<Collection, Long> {

    List<Collection> findByUser(User user);

    Optional<Collection> findByUserAndMovie(User user, Movie movie);

}
