package com.miniproject.moviebook.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequestDto {

    private String rate;
    private String content;
}
