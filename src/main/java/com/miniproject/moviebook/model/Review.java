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

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "m_id")
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "u_id")
    private User user;

    public Review(ReviewRequestDto requestDto, String name, Movie movie, User user) {
        this.rate = requestDto.getRate();
        this.content = requestDto.getContent();
        this.name = name;
        this.movie = movie;
        this.user = user;
    }

    public void update(ReviewRequestDto requestDto) {
        this.rate = requestDto.getRate();
        this.content = requestDto.getContent();
    }

}
