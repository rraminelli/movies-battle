package br.com.rraminelli.moviesbattle.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MovieDto {

    private String id;
    private String actors;
    private String plot;
    private String awards;
    private String poster;
    private String genre;

}
