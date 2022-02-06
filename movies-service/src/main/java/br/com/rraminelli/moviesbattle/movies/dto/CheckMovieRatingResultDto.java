package br.com.rraminelli.moviesbattle.movies.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckMovieRatingResultDto {

    private CheckMovieRatingDto checkMovieRating;
    private boolean result;

}
