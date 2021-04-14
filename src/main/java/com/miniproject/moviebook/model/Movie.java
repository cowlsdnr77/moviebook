package com.miniproject.moviebook.model;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "movie")
public class Movie {

    @Id
    @Column(name = "m_id")
    private Long m_id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "LONGTEXT")
    private String description;

    @Column(name = "rate", nullable = false)
    private String rate;

    @Column(name = "director", nullable = false)
    private String director;


    @Column(name = "actor1")
    private String actor1;

    @Column(name = "actor2")
    private String actor2;

    @Column(name = "actor3")
    private String actor3;

    @Column(name = "image_url")
    private String image_url;

    @Column(name = "opening_date")
    private String opening_date;

    @Column(name = "running_time")
    private String running_time;

    @Column(name = "grade")
    private String grade;

}
