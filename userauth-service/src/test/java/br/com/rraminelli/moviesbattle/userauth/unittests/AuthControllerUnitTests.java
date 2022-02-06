package br.com.rraminelli.moviesbattle.userauth.unittests;

import br.com.rraminelli.moviesbattle.userauth.application.security.CustomUserDetailsService;
import br.com.rraminelli.moviesbattle.userauth.application.security.JwtUserAuthenticationManager;
import br.com.rraminelli.moviesbattle.userauth.application.security.TokenProvider;
import br.com.rraminelli.moviesbattle.userauth.controller.AuthController;
import br.com.rraminelli.moviesbattle.userauth.dto.LoginDto;
import br.com.rraminelli.moviesbattle.userauth.dto.UserTokenDto;
import br.com.rraminelli.moviesbattle.userauth.entity.User;
import br.com.rraminelli.moviesbattle.userauth.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
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
@WebMvcTest(value = AuthController.class, includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = TokenProvider.class)})
@Slf4j
public class AuthControllerUnitTests {

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
    @Autowired
    private ObjectMapper mapper;

    @BeforeAll
    public static void init() {
        defaultUser = new User("player1", "Player 1", "1234");
        defaultUserTokenDto = new UserTokenDto(defaultUser.getUsername(), defaultUser.getName(), "token");
    }

    @Test
    void login_test() throws Exception {

        Mockito
                .when(this.jwtUserAuthenticationService.login(defaultUser.getUsername(), defaultUser.getPassword()))
                .thenReturn(defaultUserTokenDto);

        final LoginDto loginDto = new LoginDto(defaultUser.getUsername(), defaultUser.getPassword());

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is(defaultUserTokenDto.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token", Matchers.is(defaultUserTokenDto.getToken())));

    }

    @Test
    void signup_test() throws Exception {

        Mockito
                .when(this.jwtUserAuthenticationService.login(defaultUser.getUsername(), defaultUser.getPassword()))
                .thenReturn(defaultUserTokenDto);
        Mockito
                .when(this.userService.create(defaultUser))
                .thenReturn(defaultUser);
        Mockito
                .when(this.userService.save(defaultUser))
                .thenReturn(defaultUser);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/signup")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(defaultUser)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is(defaultUserTokenDto.getUsername())));

    }

    @Test
    @WithMockUser(username="player1",roles="USER")
    void refresh_token_test() throws Exception {

        Mockito
                .when(this.jwtUserAuthenticationService.refreshToken(httpServletRequest))
                .thenReturn(defaultUserTokenDto);

        final String jwtToken = tokenProvider.createToken(defaultUser.getUsername());

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/refresh-token")
                        .header("Authorization", "Bearer " + jwtToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

}
