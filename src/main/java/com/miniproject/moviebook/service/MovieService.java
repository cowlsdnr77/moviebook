package com.miniproject.moviebook.service;

import com.miniproject.moviebook.model.Movie;
import com.miniproject.moviebook.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    // 랜덤으로 영화 목록 7개 리스트 리턴하는 함수
    public List<Optional<Movie>> findRandomMovieList() {

        List<Optional<Movie>> movieList = new ArrayList<>();

        Random rand = new Random();
        Set<Long> set = new HashSet<>();

        while(set.size() < 7) {
            Long num = (long) ((int) rand.nextInt(200) + 1);
            set.add(num);
        }

        List<Long> list = new ArrayList<>(set);

        for(int i = 0; i<7 ; i++) {
            Optional<Movie> movie = movieRepository.findById(list.get(i));
            movieList.add(movie);
        }
        return movieList;
    }

}
