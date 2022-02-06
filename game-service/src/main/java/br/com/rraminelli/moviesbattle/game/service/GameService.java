package br.com.rraminelli.moviesbattle.game.service;

import br.com.rraminelli.moviesbattle.game.dto.CheckMovieRatingDto;
import br.com.rraminelli.moviesbattle.game.dto.ResumeGameDto;
import br.com.rraminelli.moviesbattle.game.dto.MoviePairDto;

import java.util.List;

public interface GameService {

    MoviePairDto startAndReturnFirstQuiz();

    MoviePairDto checkRatingAndReturnNextQuiz(CheckMovieRatingDto checkMovieRating);

    MoviePairDto getNextQuiz();

    ResumeGameDto finishAndReturnResume();

    List<ResumeGameDto> getRanking();

}
