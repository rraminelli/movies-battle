package br.com.rraminelli.moviesbattle.userauth.unittests;

import br.com.rraminelli.moviesbattle.userauth.dto.UserTokenDto;
import br.com.rraminelli.moviesbattle.userauth.entity.User;
import br.com.rraminelli.moviesbattle.userauth.repository.UserRepository;
import br.com.rraminelli.moviesbattle.userauth.service.UserService;
import br.com.rraminelli.moviesbattle.userauth.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import javax.validation.ValidationException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class UserServiceUnitTests {

    private static User defaultUser;
    private static UserTokenDto defaultUserTokenDto;

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeAll
    public static void init() {
        defaultUser = new User("player1", "Player 1", "1234");
        defaultUserTokenDto = new UserTokenDto(defaultUser.getUsername(), defaultUser.getName(), "token");
    }

    @Test
    void find_by_user_name_test() throws Exception {

        Mockito
                .when(this.userRepository.findByUsername(defaultUser.getUsername()))
                .thenReturn(java.util.Optional.ofNullable(defaultUser));

        final User user = userService.findByUsername(defaultUser.getUsername()).get();

        Assertions.assertThat(user.getUsername())
                .isNotNull()
                .isEqualTo(defaultUser.getUsername());
        Assertions.assertThat(user.getName())
                .isNotNull()
                .isEqualTo(defaultUser.getName());

    }

    @Test
    void save_test() throws Exception {

        Mockito
                .when(this.passwordEncoder.encode(defaultUser.getPassword()))
                .thenReturn("pswd_encode");
        Mockito
                .when(this.userRepository.save(defaultUser))
                .thenReturn(defaultUser);

        final User user = userService.save(defaultUser);

        Assertions.assertThat(user.getUsername())
                .isNotNull()
                .isEqualTo(defaultUser.getUsername());

    }

    @Test
    void create_test() throws Exception {

        Mockito
                .when(this.userRepository.findByUsername(defaultUser.getUsername()))
                .thenReturn(Optional.empty());
        Mockito
                .when(this.passwordEncoder.encode(defaultUser.getPassword()))
                .thenReturn("pswd_encode");
        Mockito
                .when(this.userRepository.save(defaultUser))
                .thenReturn(defaultUser);

        final User user = userService.create(defaultUser);

        Assertions.assertThat(user.getUsername())
                .isNotNull()
                .isEqualTo(defaultUser.getUsername());

        Assertions.assertThat(user.getPassword())
                .isNotNull()
                .isEqualTo("pswd_encode");

    }

    @Test
    void create_fail() throws Exception {

        Mockito
                .when(this.userRepository.findByUsername(defaultUser.getUsername()))
                .thenReturn(Optional.ofNullable(defaultUser));

        Exception exception = assertThrows(ValidationException.class, () -> {
            userService.create(defaultUser);
        });

        String actualMessage = exception.getMessage();

        Assertions.assertThat(actualMessage)
                .isNotNull()
                .isEqualTo("Username already registered");

    }

}
