package br.com.rraminelli.moviesbattle.game.integrationtests.unittests;

import br.com.rraminelli.moviesbattle.game.client.MoviesClient;
import br.com.rraminelli.moviesbattle.game.client.UserClient;
import br.com.rraminelli.moviesbattle.game.dto.*;
import br.com.rraminelli.moviesbattle.game.entity.Game;
import br.com.rraminelli.moviesbattle.game.entity.GamePoint;
import br.com.rraminelli.moviesbattle.game.repository.GamePointRepository;
import br.com.rraminelli.moviesbattle.game.repository.GameRepository;
import br.com.rraminelli.moviesbattle.game.service.impl.GameServiceImpl;
import lombok.extern.slf4j.Slf4j;
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

import javax.validation.ValidationException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@Slf4j
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
@MockitoSettings(strictness = Strictness.LENIENT)
public class QuizServiceUnitTests {

    private static UserDto defaultUser;
    private static MoviePairDto defaultMoviePairDto;

    @InjectMocks
    private GameServiceImpl gameService;
    @Mock
    private GameRepository gameRepository;
    @Mock
    private GamePointRepository gamePointRepository;
    @Mock
    private UserClient userClient;
    @Mock
    private MoviesClient moviesClient;

    @BeforeAll
    public static void init() {
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
    void start_new_game_and_get_first_movie_pair_test() throws Exception {

        final Game newGame = new Game(null, defaultUser.getUsername());
        final Game newGameSaved = new Game(1L, defaultUser.getUsername());

        Mockito
                .when(this.userClient.getCurrentUser())
                .thenReturn(defaultUser);
        Mockito
                .when(this.gamePointRepository.getQuizOpenByUsername(defaultUser.getUsername()))
                .thenReturn(null);
        Mockito
                .when(this.gameRepository.save(newGame))
                .thenReturn(newGameSaved);
        Mockito
                .when(this.moviesClient.findPairRandom(new ExcludeIdMoviesPairDto()))
                .thenReturn(defaultMoviePairDto);

        final MoviePairDto moviePairDto = gameService.startAndReturnFirstQuiz();

        assertThat(moviePairDto.getMovie1().getId())
                .isNotNull()
                .isEqualTo(defaultMoviePairDto.getMovie1().getId());

        assertThat(moviePairDto.getMovie2().getId())
                .isNotNull()
                .isEqualTo(defaultMoviePairDto.getMovie2().getId());

    }

    @Test
    void start_new_game_fail_test() throws Exception {

        final Game newGame = new Game(null, defaultUser.getUsername());
        final GamePoint gamePointOpen = new GamePoint(
                1L, newGame, "1", "2", null, null
        );
        Mockito
                .when(this.userClient.getCurrentUser())
                .thenReturn(defaultUser);
        Mockito
                .when(this.gamePointRepository.getQuizOpenByUsername(defaultUser.getUsername()))
                .thenReturn(gamePointOpen);

        Exception exception = assertThrows(ValidationException.class, () -> {
            gameService.startAndReturnFirstQuiz();
        });

        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.startsWith("There is already"));

    }

    @Test
    void check_rating_and_get_Next_Quiz_test() throws Exception {

        final Game newGame = new Game(null, defaultUser.getUsername());
        final Game newGameSaved = new Game(1L, defaultUser.getUsername());

        final CheckMovieRatingDto checkMovieRating =
                new CheckMovieRatingDto("id11", "id22", "id22");

        final CheckMovieRatingResultDto checkMovieRatingResult = new CheckMovieRatingResultDto(checkMovieRating, true);

        final GamePoint gamePointOpen = new GamePoint(
                1L, newGameSaved, checkMovieRating.getMovie1Id(), checkMovieRating.getMovie2Id(), null, null
        );

        final MoviePairDto nextQuizMoviePairDto = new MoviePairDto();
        nextQuizMoviePairDto.getMovie1().setId("111");
        nextQuizMoviePairDto.getMovie2().setId("222");

        Mockito
                .when(this.userClient.getCurrentUser())
                .thenReturn(defaultUser);
        Mockito
                .when(this.gamePointRepository.getQuizOpenByUsername(defaultUser.getUsername()))
                .thenReturn(gamePointOpen);
        Mockito
                .when(this.moviesClient.checkRating(checkMovieRating))
                .thenReturn(checkMovieRatingResult);
        Mockito
                .when(this.gameRepository.save(newGame))
                .thenReturn(newGameSaved);
        Mockito
                .when(this.moviesClient.findPairRandom(new ExcludeIdMoviesPairDto()))
                .thenReturn(nextQuizMoviePairDto);

        final MoviePairDto moviePairDto = gameService.checkRatingAndReturnNextQuiz(checkMovieRating);

        assertThat(moviePairDto.getMovie1().getId())
                .isNotNull()
                .isNotEqualTo(defaultMoviePairDto.getMovie1().getId());

        assertThat(moviePairDto.getMovie2().getId())
                .isNotNull()
                .isNotEqualTo(defaultMoviePairDto.getMovie2().getId());

        assertThat(newGame.getUsername())
                .isNotNull()
                .isEqualTo(defaultUser.getUsername());

        assertThat(gamePointOpen.getResult())
                .isNotNull()
                .isEqualTo(true);

        assertThat(gamePointOpen.getMovieCheckId())
                .isNotNull()
                .isEqualTo(checkMovieRating.getMovieCheckId());

    }

    @Test
    void check_rating_fail_test() throws Exception {

        final CheckMovieRatingDto checkMovieRating =
                new CheckMovieRatingDto("id11", "id22", "id22");

        Mockito
                .when(this.userClient.getCurrentUser())
                .thenReturn(defaultUser);
        Mockito
                .when(this.gamePointRepository.getQuizOpenByUsername(defaultUser.getUsername()))
                .thenReturn(null);

        Exception exception = assertThrows(ValidationException.class, () -> {
            gameService.checkRatingAndReturnNextQuiz(checkMovieRating);
        });

        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.startsWith("Invalid ID movies"));

    }

    @Test
    void nex_quiz_test() throws Exception {

        final MovieDto movie = new MovieDto();
        movie.setId("1");

        final Game game = new Game(1L, defaultUser.getUsername());

        final GamePoint gamePointOpen = new GamePoint(
                1L, game, "1", "2", null, null
        );

        Mockito
                .when(this.userClient.getCurrentUser())
                .thenReturn(defaultUser);
        Mockito
                .when(this.gamePointRepository.getQuizOpenByUsername(defaultUser.getUsername()))
                .thenReturn(gamePointOpen);
        Mockito
                .when(this.moviesClient.findById(gamePointOpen.getMovie1Id()))
                .thenReturn(movie);

        final MoviePairDto nextQuiz = gameService.getNextQuiz();

        assertTrue(nextQuiz.getMovie1().getId().equals(gamePointOpen.getMovie1Id()));

    }

    @Test
    void finish_test() throws Exception {

        final Game game = new Game(1L, defaultUser.getUsername());

        final CheckMovieRatingDto checkMovieRating =
                new CheckMovieRatingDto("id11", "id22", "id22");

        final GamePoint gamePointOpen = new GamePoint(
                1L, game, checkMovieRating.getMovie1Id(), checkMovieRating.getMovie2Id(), null, null
        );

        List<ResumeGameDto> resumesGame = List.of(
                new ResumeGameDto("player1", 5, 5)
        );

        Mockito
                .when(this.userClient.getCurrentUser())
                .thenReturn(defaultUser);
        Mockito
                .when(this.gamePointRepository.getQuizOpenByUsername(defaultUser.getUsername()))
                .thenReturn(gamePointOpen);
        Mockito
                .when(gamePointRepository.getResumeRanking(game.getId()))
                .thenReturn(resumesGame);

        final ResumeGameDto resumeGameDto = gameService.finishAndReturnResume();

        assertTrue(resumeGameDto.getPercent() == 50);
        assertTrue(resumeGameDto.getFaults() == 5);

    }



}
