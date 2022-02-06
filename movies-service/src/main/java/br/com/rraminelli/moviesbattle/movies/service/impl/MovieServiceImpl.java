package br.com.rraminelli.moviesbattle.movies.service.impl;

import br.com.rraminelli.moviesbattle.movies.dto.CheckMovieRatingDto;
import br.com.rraminelli.moviesbattle.movies.dto.CheckMovieRatingResultDto;
import br.com.rraminelli.moviesbattle.movies.dto.MoviePairDto;
import br.com.rraminelli.moviesbattle.movies.entity.Movie;
import br.com.rraminelli.moviesbattle.movies.repository.MovieRepository;
import br.com.rraminelli.moviesbattle.movies.service.MovieOmdbSearchService;
import br.com.rraminelli.moviesbattle.movies.service.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Slf4j
public class MovieServiceImpl implements MovieService {

    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private MovieOmdbSearchService movieOmdbSearchService;

    @PostConstruct
    public void loadAllMoviesFromApi() {
        if (this.getAll().isEmpty()) {
            movieOmdbSearchService.loadAllMovies();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Movie getById(String id) {
        return movieRepository.findById(id).get();
    }

    @Override
    @Transactional
    public List<Movie> getAll() {
        return movieRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public MoviePairDto getMoviePairGame(final List<String> excludeIdMoviesPair) {
        final List<MoviePairDto> moviePairGame = movieRepository.getMoviePairGame(excludeIdMoviesPair, Pageable.ofSize(1));
        return moviePairGame.stream().findFirst().orElse(new MoviePairDto());
    }

    @Override
    @Transactional(readOnly = true)
    public CheckMovieRatingResultDto checkMovieRating(final CheckMovieRatingDto checkMovieRating) {
        final Movie movie1 = movieRepository.getById(checkMovieRating.getMovie1Id());
        final Movie movie2 = movieRepository.getById(checkMovieRating.getMovie2Id());
        final float ratingMovie1 = movie1.getRating() * movie1.getVotes();
        final float ratingMovie2 = movie2.getRating() * movie2.getVotes();

        boolean result = (
                (movie1.getId().equals(checkMovieRating.getMovieCheckId()) && ratingMovie1 >= ratingMovie2)
                        || (movie2.getId().equals(checkMovieRating.getMovieCheckId()) && ratingMovie2 >= ratingMovie1)
        );
        return new CheckMovieRatingResultDto(checkMovieRating, result);
    }
}
