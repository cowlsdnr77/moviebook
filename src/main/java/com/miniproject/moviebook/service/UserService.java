package com.miniproject.moviebook.service;

import com.miniproject.moviebook.dto.SignupRequestDto;
import com.miniproject.moviebook.model.User;
import com.miniproject.moviebook.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /* 회원가입 */
    public User createUser(SignupRequestDto requestDto) {

        String username = requestDto.getUsername();
        Optional<User> found = userRepository.findByUsername(username);
        if (found.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자 ID 가 존재합니다.");
        }

        if (!requestDto.getPassword().equals(requestDto.getPassword_confirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        } else{
            requestDto.setPassword(bCryptPasswordEncoder.encode(requestDto.getPassword()));
            User user = new User(requestDto);
            return userRepository.save(user);
        }
    }

    /* username 중복 체크 */
    public String usernameCheck(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isPresent()) {
            return "true";
        } else {
            return "false";
        }
    }
}
