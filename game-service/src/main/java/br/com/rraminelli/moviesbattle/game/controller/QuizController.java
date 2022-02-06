package br.com.rraminelli.moviesbattle.game.controller;

import br.com.rraminelli.moviesbattle.game.dto.CheckMovieRatingDto;
import br.com.rraminelli.moviesbattle.game.dto.MoviePairDto;
import br.com.rraminelli.moviesbattle.game.dto.ResumeGameDto;
import br.com.rraminelli.moviesbattle.game.service.GameService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/quiz")
public class QuizController {

    @Autowired
    private GameService gameService;

    @ApiOperation(value = "Start a new game and get a first movie pair quiz.")
    @PutMapping("/start")
    public ResponseEntity<MoviePairDto> startGame() {
        final MoviePairDto moviePair = gameService.startAndReturnFirstQuiz();
        return ResponseEntity.ok(moviePair);
    }

    @ApiOperation("Check rating to quiz and get a first movie pair.")
    @PostMapping("/check")
    public ResponseEntity<MoviePairDto> check(@RequestBody @Valid CheckMovieRatingDto checkMovieRating) {
        final MoviePairDto moviePairDto = gameService.checkRatingAndReturnNextQuiz(checkMovieRating);
        return ResponseEntity.ok(moviePairDto);
    }

    @ApiOperation("Get a movie pair quiz.")
    @GetMapping("/next")
    public ResponseEntity<MoviePairDto> getNextQuiz() {
        final MoviePairDto moviePairDto = gameService.getNextQuiz();
        return ResponseEntity.ok(moviePairDto);
    }

    @ApiOperation(value = "Finish game and get a resume points.")
    @PutMapping("/finish")
    public ResponseEntity<ResumeGameDto> finishGame() {
        final ResumeGameDto resumeGame = gameService.finishAndReturnResume();
        return ResponseEntity.ok(resumeGame);
    }

}
