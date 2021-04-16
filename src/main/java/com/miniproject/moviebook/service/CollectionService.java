package com.miniproject.moviebook.service;

import com.miniproject.moviebook.auth.PrincipalDetails;
import com.miniproject.moviebook.model.Collection;
import com.miniproject.moviebook.model.Movie;
import com.miniproject.moviebook.model.User;
import com.miniproject.moviebook.repository.CollectionRepository;
import com.miniproject.moviebook.repository.MovieRepository;
import com.miniproject.moviebook.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    // 컬렉션 목록 반환
    public List<Collection> getCollection(Long u_id) {
        User user = userRepository.findById(u_id).orElseThrow(
                () -> new IllegalArgumentException("해당 유저 정보가 없습니다.")
        );
        List<Collection> collectionList = collectionRepository.findByUser(user);
        return collectionList;
    }

    // 컬렉션 추가
    public String addCollection(Long m_id) {
        // 현재 유저 정보
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = ((PrincipalDetails) authentication.getPrincipal()).getUser();

        Movie movie = movieRepository.findById(m_id).orElseThrow(
                () -> new IllegalArgumentException("해당 영화 정보가 없습니다.")
        );

        // Collection에 중복된 Movie 담으려고 할 때 처리
        Optional<Collection> collection_exist = collectionRepository.findByUserAndMovie(user,movie);
        if (collection_exist.isPresent()) {
            throw new IllegalArgumentException("중복된 영화는 담을 수 없습니다.");
        } else {
            Collection collection = new Collection(user,movie);
            collectionRepository.save(collection);
            return "컬렉션에 추가되었습니다.";
        }
    }

    // 컬렉션 삭제
    public String deleteCollection(Long c_id){
        Collection collection = collectionRepository.findById(c_id).orElseThrow(
                () -> new IllegalArgumentException("컬렉션 정보가 없습니다.")
        );
        // 현재 유저 정보
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = ((PrincipalDetails) authentication.getPrincipal()).getUser();

        if (user.getU_id().equals(collection.getUser().getU_id())) {
            collectionRepository.deleteById(collection.getC_id());
            return "컬렉션이 삭제되었습니다.";
        } else {
            throw new IllegalArgumentException("유저 정보가 일치하지 않습니다.");
        }
    }

}
