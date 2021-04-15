package com.miniproject.moviebook.controller;

import com.miniproject.moviebook.model.Collection;
import com.miniproject.moviebook.repository.CollectionRepository;
import com.miniproject.moviebook.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CollectionController {
    private final CollectionService collectionService;
    private final CollectionRepository collectionRepository;

    //User의 영화 컬렉션 조회
    @GetMapping("/api/collections/{u_id}")
    public List<Collection> getCollectionList(@PathVariable Long u_id) {
        return collectionService.getCollection(u_id);
    }

    //User의 영화 컬렉션에 영화 추가
    @PostMapping("/api/collections/{u_id}/{m_id}")
    public Collection addMovieToCollection(@PathVariable Long u_id, @PathVariable Long m_id) {
        return collectionService.addCollection(u_id, m_id);
    }

    //User의 영화 컬렉션에서 영화 삭제
    @DeleteMapping("/api/collections/{c_id}")
    public String deleteMovieFromCollection(@PathVariable Long c_id) {
        collectionRepository.deleteById(c_id);
        return "delete success";
    }

    // 예외 처리
    @ExceptionHandler({IllegalArgumentException.class})
    public Map<String, String> handleException(Exception e) {
        Map<String, String> map = new HashMap<>();
        map.put("errMsg", e.getMessage());
        return map;
    }

}
