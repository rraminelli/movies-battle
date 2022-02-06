package br.com.rraminelli.moviesbattle.userauth.unittests;

import br.com.rraminelli.moviesbattle.userauth.application.security.CustomUserDetailsService;
import br.com.rraminelli.moviesbattle.userauth.application.security.JwtUserAuthenticationManager;
import br.com.rraminelli.moviesbattle.userauth.application.security.TokenProvider;
import br.com.rraminelli.moviesbattle.userauth.controller.AuthUserPrincipalController;
import br.com.rraminelli.moviesbattle.userauth.controller.UserController;
import br.com.rraminelli.moviesbattle.userauth.dto.UserTokenDto;
import br.com.rraminelli.moviesbattle.userauth.entity.User;
import br.com.rraminelli.moviesbattle.userauth.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.http.HttpServletRequest;

@ContextConfiguration
@ExtendWith(SpringExtension.class)
@WebMvcTest(value = AuthUserPrincipalController.class, includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = TokenProvider.class)})
@Slf4j
public class AuthUserPrincipalControllerUnitTests {

    private static User defaultUser;
    private static UserTokenDto defaultUserTokenDto;

    @Autowired
    private TokenProvider tokenProvider;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @MockBean
    private JwtUserAuthenticationManager jwtUserAuthenticationService;
    @MockBean
    private UserService userService;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void init() {
        defaultUser = new User("player1", "Player 1", "1234");
        defaultUserTokenDto = new UserTokenDto(defaultUser.getUsername(), defaultUser.getName(), "token");
    }

    @Test
    @WithMockUser(username="player1",roles="USER")
    void current_user_test() throws Exception {

        final String jwtToken = tokenProvider.createToken(defaultUser.getUsername());

        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/auth-server")
                        .header("Authorization", "Bearer " + jwtToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

}
