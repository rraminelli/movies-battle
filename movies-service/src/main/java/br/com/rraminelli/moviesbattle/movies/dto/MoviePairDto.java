package br.com.rraminelli.moviesbattle.movies.dto;

import br.com.rraminelli.moviesbattle.movies.entity.Movie;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoviePairDto {

    private Movie movie1;
    private Movie movie2;

}
