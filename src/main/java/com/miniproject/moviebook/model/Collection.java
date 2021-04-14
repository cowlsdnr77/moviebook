package com.miniproject.moviebook.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Collection {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "c_id")
    private Long c_id;

    @ManyToOne
    @JoinColumn(name = "u_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "m_id")
    private Movie movie;


    public Collection(User user, Movie movie) {
        this.user = user;
        this.movie = movie;
    }
}
