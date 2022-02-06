package br.com.rraminelli.moviesbattle.movies.unittests;

import br.com.rraminelli.moviesbattle.movies.dto.CheckMovieRatingDto;
import br.com.rraminelli.moviesbattle.movies.dto.CheckMovieRatingResultDto;
import br.com.rraminelli.moviesbattle.movies.dto.ExcludeIdMoviesPairDto;
import br.com.rraminelli.moviesbattle.movies.dto.MoviePairDto;
import br.com.rraminelli.moviesbattle.movies.entity.Movie;
import br.com.rraminelli.moviesbattle.movies.repository.MovieRepository;
import br.com.rraminelli.moviesbattle.movies.service.impl.MovieOmdbSearchServiceImpl;
import br.com.rraminelli.moviesbattle.movies.service.impl.MovieServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import javax.validation.ValidationException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
@MockitoSettings(strictness = Strictness.LENIENT)
public class MovieServiceUnitTests {

    private static MoviePairDto defaultMoviePairDto;

    @InjectMocks
    private MovieOmdbSearchServiceImpl movieOmdbSearchService;
    @InjectMocks
    private MovieServiceImpl movieService;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private RestTemplate restTemplate;

    @BeforeAll
    public static void init() {
        defaultMoviePairDto = new MoviePairDto();
        Movie movie1 = new Movie();
        movie1.setId("id1");
        defaultMoviePairDto.setMovie1(movie1);

        Movie movie2 = new Movie();
        movie2.setId("id2");
        defaultMoviePairDto.setMovie2(movie2);
    }

    @Test
    void find_all_movies_test() throws Exception {

        final Movie movie = new Movie();
        movie.setId("1");

        Mockito
                .when(this.movieRepository.findAll())
                .thenReturn(List.of(movie));

        final List<Movie> allMovies = movieService.getAll();

        assertFalse(allMovies.isEmpty());

    }

    @Test
    void load_all_movies_test() throws Exception {

        final Movie movie = new Movie();
        movie.setId("1");

        Mockito
                .when(this.movieRepository.findAll())
                .thenReturn(List.of(movie));

        assertDoesNotThrow(() -> movieService.loadAllMoviesFromApi());

    }

    @Test
    void load_all_movies_api_test() throws Exception {

        ReflectionTestUtils.setField(movieOmdbSearchService,
                "urlOmdbapi",
                "http://www.omdbapi.com?apikey=ef08615e");

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        final HttpEntity request = new HttpEntity(headers);

        final ParameterizedTypeReference<Map<String, Object>> responseType = new ParameterizedTypeReference<>() {};

        final Map<String, Object> bodyResponseMovieId = Map.of(
                "Search", List.of(Map.of("imdbID", "id1"))
        );

        final ResponseEntity<Map<String, Object>> responseMovieId = new ResponseEntity(bodyResponseMovieId, HttpStatus.OK);

        final Map<String, Object> bodyResponseMovie = Map.of(
                "imdbID", "id1",
                "imdbRating","10.0",
                "imdbVotes", "10",
                "Actors", "a",
                "Plot", "p",
                "Awards", "a",
                "Poster", "p",
                "Genre", "g"
        );

        final ResponseEntity<Map<String, Object>> responseMovie = new ResponseEntity(bodyResponseMovie, HttpStatus.OK);

        Mockito
                .when(restTemplate.exchange("http://www.omdbapi.com?apikey=ef08615e&type=movie&s=title&page=1",
                        HttpMethod.GET,
                        request,
                        responseType)
                )
                .thenReturn(responseMovieId);

        Mockito
                .when(restTemplate.exchange("http://www.omdbapi.com?apikey=ef08615e&i=id1",
                        HttpMethod.GET,
                        request,
                        responseType)
                )
                .thenReturn(responseMovie);

        assertDoesNotThrow(() -> movieOmdbSearchService.loadAllMovies());

    }

    @Test
    void get_by_id_test() throws Exception {

        final Movie movie = new Movie();
        movie.setId("1");

        Mockito
                .when(this.movieRepository.findById(movie.getId()))
                .thenReturn(Optional.ofNullable(movie));

        final Movie movieSelect = movieService.getById(movie.getId());

        assertTrue(movie.getId().equals(movieSelect.getId()));

    }

    @Test
    void get_movies_pair_test() throws Exception {

        final Movie movie = new Movie();
        movie.setId("1");

        final List<String> excludeIdsMovie = List.of("4;5");

        Mockito
                .when(this.movieRepository.getMoviePairGame(excludeIdsMovie, null))
                .thenReturn(List.of(defaultMoviePairDto));

        final MoviePairDto moviePairGame = movieService.getMoviePairGame(excludeIdsMovie);

        assertTrue(moviePairGame != null);
        assertTrue(!excludeIdsMovie.get(0).contains(defaultMoviePairDto.getMovie1().getId()));

    }

    @Test
    void check_rating_test() throws Exception {

        final Movie movie = new Movie("id11", 1f, 2f, "a", "p", "a", "p", "g");

        final CheckMovieRatingDto checkMovieRating =
                new CheckMovieRatingDto("id11", "id22", "id11");

        Mockito
                .when(this.movieRepository.getById("id11"))
                .thenReturn(movie);
        Mockito
                .when(this.movieRepository.getById("id22"))
                .thenReturn(movie);

        final CheckMovieRatingResultDto checkMovieRatingResult = movieService.checkMovieRating(checkMovieRating);

        assertTrue(checkMovieRatingResult.isResult());

    }


}
