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

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthIntegrationTest {

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
    public void login_test() {

        final LoginDto loginDto = new LoginDto("player1", "1234");

        HttpEntity<LoginDto> httpEntity = new HttpEntity<>(loginDto);

        ResponseEntity<UserTokenDto> response = this.testRestTemplate
                .exchange("/auth/login", HttpMethod.POST, httpEntity, UserTokenDto.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getUsername(), loginDto.getUsername());

    }

    @Test
    public void signup_user_test() {

        final User user = new User("player1test", "Player 1 test", "1234");

        HttpEntity<User> httpEntity = new HttpEntity<>(user);

        ResponseEntity<UserTokenDto> response = this.testRestTemplate
                .exchange("/auth/signup", HttpMethod.POST, httpEntity, UserTokenDto.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getUsername(), user.getUsername());

    }

    @Test
    public void refresh_token_test() {

        final String token = tokenProvider.createToken(USER_LOGIN);
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        final HttpEntity httpEntity = new HttpEntity<>(null, headers);

        ResponseEntity<UserTokenDto> response = this.testRestTemplate
                .exchange("/auth/refresh-token", HttpMethod.POST, httpEntity, UserTokenDto.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getUsername(), USER_LOGIN);

    }

}
