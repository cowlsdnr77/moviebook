package com.miniproject.moviebook.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignupRequestDto {
    private String username; //email 값
    private String password;
    private String password_confirm;
    private String name;
}
