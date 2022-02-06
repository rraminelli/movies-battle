package br.com.rraminelli.moviesbattle.movies.unittests;

import br.com.rraminelli.moviesbattle.movies.controller.MoviesController;
import br.com.rraminelli.moviesbattle.movies.dto.CheckMovieRatingDto;
import br.com.rraminelli.moviesbattle.movies.dto.CheckMovieRatingResultDto;
import br.com.rraminelli.moviesbattle.movies.dto.ExcludeIdMoviesPairDto;
import br.com.rraminelli.moviesbattle.movies.dto.MoviePairDto;
import br.com.rraminelli.moviesbattle.movies.entity.Movie;
import br.com.rraminelli.moviesbattle.movies.service.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

@ContextConfiguration
@ExtendWith(SpringExtension.class)
@WebMvcTest(value = MoviesController.class)
@Slf4j
public class MovieControllerUnitTests {

    private static Movie defaultMovie;
    private static MoviePairDto defaultMoviePairDto;

    @MockBean
    private MovieService movieService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @BeforeAll
    public static void init() {
        defaultMovie = new Movie("mov1", 10f, 3f, "ac", "pl", "aw", "po", "ge");

        defaultMoviePairDto = new MoviePairDto();
        Movie movie1 = new Movie();
        movie1.setId("id1");
        defaultMoviePairDto.setMovie1(movie1);

        Movie movie2 = new Movie();
        movie2.setId("id2");
        defaultMoviePairDto.setMovie2(movie2);
    }

    @Test
    void list_movies_test() throws Exception {

        final List<Movie> movies = List.of(defaultMovie);

        Mockito
                .when(this.movieService.getAll())
                .thenReturn(movies);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/movie")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    void get_movie_id_test() throws Exception {

        Mockito
                .when(this.movieService.getById(defaultMovie.getId()))
                .thenReturn(defaultMovie);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/movie/"+defaultMovie.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    void find_pair_random_test() throws Exception {

        Mockito
                .when(this.movieService.getMoviePairGame(new ArrayList<>()))
                .thenReturn(defaultMoviePairDto);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/movie/pair-random")
                        .content(mapper.writeValueAsString(new ExcludeIdMoviesPairDto()))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    void check_test() throws Exception {

        final CheckMovieRatingDto checkMovieRating =
                new CheckMovieRatingDto("id11", "id22", "id22");

        final CheckMovieRatingResultDto checkMovieRatingResult = new CheckMovieRatingResultDto(checkMovieRating, true);

        Mockito
                .when(this.movieService.checkMovieRating(checkMovieRating))
                .thenReturn(checkMovieRatingResult);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/movie/check-rating")
                        .content(mapper.writeValueAsString(checkMovieRating))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

}
