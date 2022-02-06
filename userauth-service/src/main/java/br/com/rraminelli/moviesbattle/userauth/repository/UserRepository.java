package br.com.rraminelli.moviesbattle.userauth.repository;

import br.com.rraminelli.moviesbattle.userauth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);

}
