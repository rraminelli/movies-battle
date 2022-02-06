package br.com.rraminelli.moviesbattle.game.integrationtests.unittests;

import br.com.rraminelli.moviesbattle.game.client.MoviesClient;
import br.com.rraminelli.moviesbattle.game.client.UserClient;
import br.com.rraminelli.moviesbattle.game.controller.QuizController;
import br.com.rraminelli.moviesbattle.game.dto.*;
import br.com.rraminelli.moviesbattle.game.entity.Game;
import br.com.rraminelli.moviesbattle.game.entity.GamePoint;
import br.com.rraminelli.moviesbattle.game.repository.GamePointRepository;
import br.com.rraminelli.moviesbattle.game.repository.GameRepository;
import br.com.rraminelli.moviesbattle.game.service.GameService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ContextConfiguration
@ExtendWith(SpringExtension.class)
@WebMvcTest(value = QuizController.class)
@Slf4j
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
public class QuizControllerUnitTests {

    private static MoviePairDto defaultMoviePairDto;
    private static UserDto defaultUser;

    @MockBean
    private MoviesClient moviesClient;
    @MockBean
    private GameService gameService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @BeforeAll
    public static void init(){
        defaultUser = new UserDto("player1", "Player 1");

        defaultMoviePairDto = new MoviePairDto();
        MovieDto movie1 = new MovieDto();
        movie1.setId("id1");
        defaultMoviePairDto.setMovie1(movie1);

        MovieDto movie2 = new MovieDto();
        movie2.setId("id2");
        defaultMoviePairDto.setMovie2(movie2);
    }

    @Test
    void start_game_test() throws Exception {

        Mockito
                .when(this.gameService.startAndReturnFirstQuiz())
                .thenReturn(defaultMoviePairDto);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/quiz/start")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.movie1.id", is(defaultMoviePairDto.getMovie1().getId())))
                .andExpect(jsonPath("$.movie2.id", is(defaultMoviePairDto.getMovie2().getId())));

    }

    @Test
    void check_test() throws Exception {

        final CheckMovieRatingDto checkMovieRating =
                new CheckMovieRatingDto("id11", "id22", "id22");

        final CheckMovieRatingResultDto checkMovieRatingResult = new CheckMovieRatingResultDto(checkMovieRating, true);

        Mockito
                .when(this.moviesClient.checkRating(checkMovieRating))
                .thenReturn(checkMovieRatingResult);

        Mockito
                .when(this.gameService.checkRatingAndReturnNextQuiz(checkMovieRating))
                .thenReturn(defaultMoviePairDto);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/quiz/check")
                        .content(mapper.writeValueAsString(checkMovieRating))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    void next_quiz_test() throws Exception {

        Mockito
                .when(this.gameService.getNextQuiz())
                .thenReturn(defaultMoviePairDto);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/quiz/next")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.movie1.id", is(defaultMoviePairDto.getMovie1().getId())))
                .andExpect(jsonPath("$.movie2.id", is(defaultMoviePairDto.getMovie2().getId())));

    }

    @Test
    void finish_test() throws Exception {

        final ResumeGameDto resumeGame = new ResumeGameDto("player1", 10, 0);

        Mockito
                .when(this.gameService.finishAndReturnResume())
                .thenReturn(resumeGame);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/quiz/finish")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.username", is(resumeGame.getUsername())))
                .andExpect(jsonPath("$.percent", is(100.0)));

    }

}
