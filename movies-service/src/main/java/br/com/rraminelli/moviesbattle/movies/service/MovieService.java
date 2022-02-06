package br.com.rraminelli.moviesbattle.movies.service;

import br.com.rraminelli.moviesbattle.movies.dto.CheckMovieRatingDto;
import br.com.rraminelli.moviesbattle.movies.dto.CheckMovieRatingResultDto;
import br.com.rraminelli.moviesbattle.movies.dto.MoviePairDto;
import br.com.rraminelli.moviesbattle.movies.entity.Movie;

import java.util.List;

public interface MovieService {

    Movie getById(String id);

    List<Movie> getAll();

    MoviePairDto getMoviePairGame(List<String> excludeIdMoviesPair);

    CheckMovieRatingResultDto checkMovieRating(CheckMovieRatingDto checkMovieRating);

}
