package br.com.rraminelli.moviesbattle.userauth.service;

import br.com.rraminelli.moviesbattle.userauth.entity.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findByUsername(String username);

    User save(User user);

    User create(User user);

}
