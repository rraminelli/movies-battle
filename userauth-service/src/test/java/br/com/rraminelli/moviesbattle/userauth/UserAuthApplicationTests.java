package br.com.rraminelli.moviesbattle.userauth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest({"server.port:0", "eureka.client.enabled:false"})
public class UserAuthApplicationTests {

    @Test
    void contextLoads() {
    }

}
