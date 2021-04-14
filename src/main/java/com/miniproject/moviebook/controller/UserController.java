package com.miniproject.moviebook.controller;

import com.miniproject.moviebook.dto.SignupRequestDto;
import com.miniproject.moviebook.model.User;
import com.miniproject.moviebook.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/signup")
    public User createUser(@RequestBody SignupRequestDto signupRequestDto) {
        return userService.createUser(signupRequestDto);
    }
}
