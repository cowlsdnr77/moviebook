package com.miniproject.moviebook.service;

import com.miniproject.moviebook.model.Collection;
import com.miniproject.moviebook.model.Movie;
import com.miniproject.moviebook.model.User;
import com.miniproject.moviebook.repository.CollectionRepository;
import com.miniproject.moviebook.repository.MovieRepository;
import com.miniproject.moviebook.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    // 컬렉션 목록 반환
    public List<Collection> getCollection(Long u_id) {
        User user = userRepository.findById(u_id).orElseThrow(
                () -> new IllegalArgumentException("user Error")
        );
        List<Collection> collectionList = collectionRepository.findByUser(user);
        return collectionList;
    }

    // 컬렉션 추가
    public Collection addCollection(Long u_id, Long m_id) {
        User user = userRepository.findById(u_id).orElseThrow(
                () -> new IllegalArgumentException("user Error")
        );

        Movie movie = movieRepository.findById(m_id).orElseThrow(
                () -> new IllegalArgumentException("movie Error")
        );

        Collection collection = new Collection(user,movie);
        collectionRepository.save(collection);

        return collection;
    }



}
