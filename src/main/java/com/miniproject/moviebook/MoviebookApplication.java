package com.miniproject.moviebook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MoviebookApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoviebookApplication.class, args);
	}

}
