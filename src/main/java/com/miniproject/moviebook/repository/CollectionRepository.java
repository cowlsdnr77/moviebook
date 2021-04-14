package com.miniproject.moviebook.repository;

import com.miniproject.moviebook.model.Collection;
import com.miniproject.moviebook.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollectionRepository extends JpaRepository<Collection, Long> {

    List<Collection> findByUser(User user);

}
