package com.miniproject.moviebook.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoDto {
    private Long u_id;
    private String name;


    public UserInfoDto(Long u_id, String name) {
        this.u_id = u_id;
        this.name = name;

    }
}
