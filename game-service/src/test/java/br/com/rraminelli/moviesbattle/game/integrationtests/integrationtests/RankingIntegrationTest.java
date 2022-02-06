package br.com.rraminelli.moviesbattle.game.integrationtests.integrationtests;

import br.com.rraminelli.moviesbattle.game.repository.GamePointRepository;
import br.com.rraminelli.moviesbattle.game.repository.GameRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RankingIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GamePointRepository gamePointRepository;

    private final static String USER_LOGIN = "player1";

    @BeforeAll
    public void init() {
    }

    @Test
    public void ranking_test() {

        ResponseEntity<List> response = this.testRestTemplate
                .exchange("/ranking", HttpMethod.GET, null, List.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);

    }

}
