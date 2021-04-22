package com.miniproject.moviebook.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Getter
@NoArgsConstructor
@Table(name = "refresh_token")
@Entity
public class RefreshToken {

    @Id
    private Long rt_id;
    private String value;

    public RefreshToken updateValue(String token) {
        this.value = token;
        return this;
    }

    public RefreshToken(Long rt_id, String value) {
        this.rt_id = rt_id;
        this.value = value;
    }
}