package com.miniproject.moviebook.service;

import com.miniproject.moviebook.dto.SignupRequestDto;
import com.miniproject.moviebook.model.User;
import com.miniproject.moviebook.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
//    private final BCryptPasswordEncoder bCryptPasswordEncoder; 스프링 시큐리티 설치해야 사용가능

    /* 회원가입 */
    public User createUser(SignupRequestDto signupRequestDto) {
        User user = new User(signupRequestDto);
        return userRepository.save(user);
    }
}
