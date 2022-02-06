package br.com.rraminelli.moviesbattle.userauth.integrationtests;

import br.com.rraminelli.moviesbattle.userauth.application.security.TokenProvider;
import br.com.rraminelli.moviesbattle.userauth.dto.LoginDto;
import br.com.rraminelli.moviesbattle.userauth.dto.UserTokenDto;
import br.com.rraminelli.moviesbattle.userauth.entity.User;
import br.com.rraminelli.moviesbattle.userauth.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenProvider tokenProvider;

    private final static String USER_LOGIN = "player1";

    @BeforeAll
    public void init() {
    }

    @Test
    public void current_user_test() {

        final String token = tokenProvider.createToken(USER_LOGIN);
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        final HttpEntity httpEntity = new HttpEntity<>(null, headers);

        ResponseEntity<User> response = this.testRestTemplate
                .exchange("/user/current-user", HttpMethod.GET, httpEntity, User.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getUsername(), USER_LOGIN);

    }

}
