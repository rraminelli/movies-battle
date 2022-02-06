package br.com.rraminelli.moviesbattle.game.service.impl;

import br.com.rraminelli.moviesbattle.game.client.MoviesClient;
import br.com.rraminelli.moviesbattle.game.client.UserClient;
import br.com.rraminelli.moviesbattle.game.dto.*;
import br.com.rraminelli.moviesbattle.game.entity.Game;
import br.com.rraminelli.moviesbattle.game.entity.GamePoint;
import br.com.rraminelli.moviesbattle.game.repository.GamePointRepository;
import br.com.rraminelli.moviesbattle.game.repository.GameRepository;
import br.com.rraminelli.moviesbattle.game.service.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class GameServiceImpl implements GameService {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GamePointRepository gamePointRepository;
    @Autowired
    private UserClient userClient;
    @Autowired
    private MoviesClient moviesClient;

    private static final String MSG_GAME_IS_OPEN = "There is already an open game, finish or use /next-quiz";
    private static final String MSG_3_QUIZ_ERRORS = "Maximum of 3 errors overcome. Finish the game with /finish";
    private static final String MSG_INVALID_ID_MOVIES = "Invalid ID movies, finish quiz or use /next-quiz";
    private static final String MSG_QUIZ_NOT_FOUND = "Quiz not found, start a new game";

    @Override
    @Transactional
    public MoviePairDto startAndReturnFirstQuiz() {
        final UserDto currentUser = userClient.getCurrentUser();
        final GamePoint gamePointOpen = gamePointRepository.getQuizOpenByUsername(currentUser.getUsername());
        if (gamePointOpen != null && gamePointOpen.getId() != null) {
            throw new ValidationException(MSG_GAME_IS_OPEN);
        }

        Game newGame = new Game();
        newGame.setUsername(currentUser.getUsername());
        newGame = gameRepository.save(newGame);

        return this.createNewQuiz(newGame);
    }

    @Override
    @Transactional
    public MoviePairDto checkRatingAndReturnNextQuiz(CheckMovieRatingDto checkMovieRating) {
        final UserDto currentUser = userClient.getCurrentUser();
        final GamePoint gamePointOpen = gamePointRepository.getQuizOpenByUsername(currentUser.getUsername());
        if (gamePointOpen == null || (!gamePointOpen.getMovie1Id().equals(checkMovieRating.getMovie1Id()) || !gamePointOpen.getMovie2Id().equals(checkMovieRating.getMovie2Id()))) {
            throw new ValidationException(MSG_INVALID_ID_MOVIES);
        }

        final CheckMovieRatingResultDto checkMovieRatingResult = moviesClient.checkRating(checkMovieRating);
        gamePointOpen.setMovieCheckId(checkMovieRating.getMovieCheckId());
        gamePointOpen.setResult(checkMovieRatingResult.isResult());
        gamePointRepository.save(gamePointOpen);

        final ResumeGameDto resumeGame = this.getResumeGame(gamePointOpen.getGame());
        if (resumeGame.getFaults() >= 3) {
            throw new ValidationException(MSG_3_QUIZ_ERRORS);
        }

        return this.createNewQuiz(gamePointOpen.getGame());
    }

    private MoviePairDto createNewQuiz(final Game game) {
        final ExcludeIdMoviesPairDto excludeIdMoviesPair = new ExcludeIdMoviesPairDto();
        excludeIdMoviesPair.setExcludeIdMoviesPair(gamePointRepository.getIdsMoviesQuizByGameId(game.getId()));
        final MoviePairDto moviePairRandom = moviesClient.findPairRandom(excludeIdMoviesPair);

        final GamePoint gamePoint = new GamePoint();
        gamePoint.setGame(game);
        gamePoint.setMovie1Id(moviePairRandom.getMovie1().getId());
        gamePoint.setMovie2Id(moviePairRandom.getMovie2().getId());

        gamePointRepository.save(gamePoint);

        return moviePairRandom;
    }

    @Override
    @Transactional(readOnly = true)
    public MoviePairDto getNextQuiz() {
        final GamePoint gamePointOpen = this.getGamePointOpen();

        final MoviePairDto moviePairQuiz = new MoviePairDto();
        moviePairQuiz.setMovie1(moviesClient.findById(gamePointOpen.getMovie1Id()));
        moviePairQuiz.setMovie2(moviesClient.findById(gamePointOpen.getMovie2Id()));

        return moviePairQuiz;
    }

    @Override
    @Transactional
    public ResumeGameDto finishAndReturnResume() {
        final GamePoint gamePointOpen = this.getGamePointOpen();
        gamePointRepository.delete(gamePointOpen);
        return this.getResumeGame(gamePointOpen.getGame());
    }

    private GamePoint getGamePointOpen() {
        final UserDto currentUser = userClient.getCurrentUser();
        final GamePoint gamePointOpen = gamePointRepository.getQuizOpenByUsername(currentUser.getUsername());
        if (gamePointOpen == null || gamePointOpen.getId() == null) {
            throw new ValidationException(MSG_QUIZ_NOT_FOUND);
        }
        return gamePointOpen;
    }

    private ResumeGameDto getResumeGame(final Game game) {
        return gamePointRepository.getResumeRanking(game.getId())
                .stream().findFirst()
                .orElse(new ResumeGameDto());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResumeGameDto> getRanking() {
        final List<ResumeGameDto> resumeRanking = gamePointRepository.getResumeRanking(null);
        resumeRanking.sort(Comparator.comparingDouble(ResumeGameDto::getPercent).reversed());
        return resumeRanking;
    }

}
