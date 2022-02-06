package br.com.rraminelli.moviesbattle.game.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class ExcludeIdMoviesPairDto {

    private List<String> excludeIdMoviesPair = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExcludeIdMoviesPairDto that = (ExcludeIdMoviesPairDto) o;
        return excludeIdMoviesPair.equals(that.excludeIdMoviesPair);
    }

}
