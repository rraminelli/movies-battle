package br.com.rraminelli.moviesbattle.game.client;

import br.com.rraminelli.moviesbattle.game.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@FeignClient("movies")
public interface MoviesClient {

    @RequestMapping(value = "/movie/pair-random", method = RequestMethod.POST)
    MoviePairDto findPairRandom(@RequestBody @Valid ExcludeIdMoviesPairDto excludeIdMoviesPair);

    @RequestMapping(value = "/movie/check-rating", method = RequestMethod.POST)
    CheckMovieRatingResultDto checkRating(@RequestBody @Valid CheckMovieRatingDto checkMovieRatingDto);

    @RequestMapping(value = "/movie/{id}", method = RequestMethod.GET)
    MovieDto findById(@PathVariable("id") String id);

}
