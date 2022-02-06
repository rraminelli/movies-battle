package br.com.rraminelli.moviesbattle.game.integrationtests;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest({"server.port:0", "eureka.client.enabled:false"})
public class GameApplicationTests {

    @Test
    void contextLoads() {
    }

}
