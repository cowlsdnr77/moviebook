package com.miniproject.moviebook.service;

import com.miniproject.moviebook.dto.SignupRequestDto;
import com.miniproject.moviebook.model.User;
import com.miniproject.moviebook.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
//    private final BCryptPasswordEncoder bCryptPasswordEncoder; 스프링 시큐리티 설치해야 사용가능

    /* 회원가입 */
    public User createUser(SignupRequestDto requestDto) {

        String username = requestDto.getUsername();
        Optional<User> found = userRepository.findByUsername(username);
        if (found.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자 ID 가 존재합니다.");
        }

        User user = new User(requestDto);
        return userRepository.save(user);
    }
}
