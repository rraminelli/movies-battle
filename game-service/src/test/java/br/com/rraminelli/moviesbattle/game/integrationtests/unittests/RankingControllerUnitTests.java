package br.com.rraminelli.moviesbattle.game.integrationtests.unittests;

import br.com.rraminelli.moviesbattle.game.controller.RankingController;
import br.com.rraminelli.moviesbattle.game.dto.ResumeGameDto;
import br.com.rraminelli.moviesbattle.game.service.GameService;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ContextConfiguration
@ExtendWith(SpringExtension.class)
@WebMvcTest(value = RankingController.class)
@Slf4j
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
public class RankingControllerUnitTests {

    @MockBean
    private GameService gameService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void ranking_test() throws Exception {

        List<ResumeGameDto> resumesGame = List.of(
                new ResumeGameDto("player1", 5, 5)
        );

        Mockito
                .when(this.gameService.getRanking())
                .thenReturn(resumesGame);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/ranking")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$", hasSize(resumesGame.size())))
                .andExpect(jsonPath("$.[0].percent", is(50.0)));

    }

}
