package com.miniproject.moviebook.controller;

import com.miniproject.moviebook.model.Movie;
import com.miniproject.moviebook.repository.MovieRepository;
import com.miniproject.moviebook.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MovieController {

    private final MovieRepository movieRepository;
    private final MovieService movieService;

    // 무작위로 10개 정도 영화 목록 가져오기
    @GetMapping("/api/movies/random")
    public List<Optional<Movie>> GetRandomMovies() {
        return movieService.findRandomMovieList();
    }

    // 영화 상세 정보 조회
    @GetMapping("/api/movies/details/{m_id}")
    public Optional<Movie> GetMovieDetails(@PathVariable Long m_id) {
        return movieRepository.findById(m_id);
    }

    // 영화 검색(부분 단어로 검색)된 목록 가져오기
    @GetMapping("/api/movies")
    public Optional<List<Movie>> SearchMovie(@RequestParam(value = "search") String search) {
        return movieRepository.findByTitleLike("%" + search + "%");
    }

}
