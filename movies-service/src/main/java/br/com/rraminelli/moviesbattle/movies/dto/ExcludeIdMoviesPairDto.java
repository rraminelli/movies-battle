package br.com.rraminelli.moviesbattle.movies.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ExcludeIdMoviesPairDto {

    @NotNull
    private List<String> excludeIdMoviesPair = new ArrayList<>();

}
