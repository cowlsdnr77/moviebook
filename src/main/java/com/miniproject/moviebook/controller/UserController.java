package com.miniproject.moviebook.controller;

import com.miniproject.moviebook.dto.SignupRequestDto;
import com.miniproject.moviebook.model.User;
import com.miniproject.moviebook.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/signup")
    public User createUser(@RequestBody SignupRequestDto signupRequestDto) {
        return userService.createUser(signupRequestDto);
    }

    @GetMapping("/api/signup/{username}")
    public String usernameCheck(@PathVariable String username) {
        return userService.usernameCheck(username);
    }

    // 예외 처리
    @ExceptionHandler({IllegalArgumentException.class})
    public Map<String, String> handleException(Exception e) {
        Map<String, String> map = new HashMap<>();
        map.put("errMsg", e.getMessage());
        return map;
    }
}
