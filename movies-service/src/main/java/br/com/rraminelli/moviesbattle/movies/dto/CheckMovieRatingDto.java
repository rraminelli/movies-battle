package br.com.rraminelli.moviesbattle.movies.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckMovieRatingDto {

    @NotEmpty
    private String movie1Id;
    @NotEmpty
    private String movie2Id;
    @NotEmpty
    private String movieCheckId;

}
