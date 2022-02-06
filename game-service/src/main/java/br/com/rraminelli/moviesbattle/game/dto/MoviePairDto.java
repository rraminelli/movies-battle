package br.com.rraminelli.moviesbattle.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MoviePairDto {

    private MovieDto movie1 = new MovieDto();
    private MovieDto movie2 = new MovieDto();

}
