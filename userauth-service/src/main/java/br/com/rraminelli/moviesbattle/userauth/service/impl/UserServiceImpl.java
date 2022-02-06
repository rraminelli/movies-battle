package br.com.rraminelli.moviesbattle.userauth.service.impl;

import br.com.rraminelli.moviesbattle.userauth.entity.User;
import br.com.rraminelli.moviesbattle.userauth.repository.UserRepository;
import br.com.rraminelli.moviesbattle.userauth.service.UserService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.validation.ValidationException;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    private static final String MSG_USERNAME_IS_REGISTERED = "Username already registered";

    @PostConstruct
    private void createInitialUsers() {
        final User userPlayer1 = new User();
        userPlayer1.setUsername("player1");
        userPlayer1.setPassword("1234");
        userPlayer1.setName("Player 1");
        this.save(userPlayer1);

        final User userPlayer2 = new User();
        userPlayer2.setUsername("player2");
        userPlayer2.setPassword("1234");
        userPlayer2.setName("Player 2");
        this.save(userPlayer2);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public User save(User user) {
        if (Strings.isNotEmpty(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User create(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new ValidationException(MSG_USERNAME_IS_REGISTERED);
        }
        return this.save(user);
    }


}
