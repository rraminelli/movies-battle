package br.com.rraminelli.moviesbattle.game.controller;

import br.com.rraminelli.moviesbattle.game.dto.ResumeGameDto;
import br.com.rraminelli.moviesbattle.game.service.GameService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ranking")
public class RankingController {

    @Autowired
    private GameService gameService;

    @ApiOperation("Get the ranking list user.")
    @GetMapping
    public ResponseEntity<List<ResumeGameDto>> getRanking() {
        final List<ResumeGameDto> ranking = gameService.getRanking();
        return ResponseEntity.ok(ranking);
    }

}
