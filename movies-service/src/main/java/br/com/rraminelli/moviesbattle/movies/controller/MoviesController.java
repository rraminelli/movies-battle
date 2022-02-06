package br.com.rraminelli.moviesbattle.movies.controller;

import br.com.rraminelli.moviesbattle.movies.dto.CheckMovieRatingDto;
import br.com.rraminelli.moviesbattle.movies.dto.CheckMovieRatingResultDto;
import br.com.rraminelli.moviesbattle.movies.dto.ExcludeIdMoviesPairDto;
import br.com.rraminelli.moviesbattle.movies.dto.MoviePairDto;
import br.com.rraminelli.moviesbattle.movies.entity.Movie;
import br.com.rraminelli.moviesbattle.movies.service.MovieService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/movie")
public class MoviesController {

    @Autowired
    private MovieService movieService;

    @ApiOperation(value = "List all movies from OMDB-API http://www.omdbapi.com/")
    @GetMapping
    public ResponseEntity<List<Movie>> findAll() {
        final List<Movie> movies = movieService.getAll();
        return ResponseEntity.ok(movies);
    }

    @ApiOperation("Get a movie data from OMDB-API http://www.omdbapi.com/")
    @GetMapping("/{id}")
    public ResponseEntity<Movie> findById(@PathVariable("id") String id) {
        final Movie movie = movieService.getById(id);
        return ResponseEntity.ok(movie);
    }

    @ApiOperation("Get a movie pair for game.")
    @PostMapping("/pair-random")
    public ResponseEntity<MoviePairDto> findPairRandom(@RequestBody @Valid ExcludeIdMoviesPairDto excludeIdMoviesPair) {
        final MoviePairDto moviePairDto = movieService.getMoviePairGame(excludeIdMoviesPair.getExcludeIdMoviesPair());
        return ResponseEntity.ok(moviePairDto);
    }

    @ApiOperation("Check vote rating.")
    @PostMapping("/check-rating")
    public ResponseEntity<CheckMovieRatingResultDto> checkRating(@RequestBody @Valid CheckMovieRatingDto checkMovieRatingDto) {
        final CheckMovieRatingResultDto checkMovieRatingResult = movieService.checkMovieRating(checkMovieRatingDto);
        return ResponseEntity.ok(checkMovieRatingResult);
    }

}
