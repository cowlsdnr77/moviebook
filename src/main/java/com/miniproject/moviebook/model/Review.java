package com.miniproject.moviebook.model;

import com.miniproject.moviebook.dto.ReviewRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "review")
@NoArgsConstructor
public class Review extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "r_id")
    private Long r_id;

    @Column(name = "rate", nullable = false)
    private String rate;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "m_id")
    private Movie movie;

    public Review(ReviewRequestDto requestDto, String username, Movie movie) {
        this.rate = requestDto.getRate();
        this.content = requestDto.getContent();
        this.username = username;
        this.movie = movie;
    }

}
