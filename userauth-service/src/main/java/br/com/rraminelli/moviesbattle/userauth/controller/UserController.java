package br.com.rraminelli.moviesbattle.userauth.controller;

import br.com.rraminelli.moviesbattle.userauth.application.security.JwtSecurityUtils;
import br.com.rraminelli.moviesbattle.userauth.application.security.JwtUserAuthenticationManager;
import br.com.rraminelli.moviesbattle.userauth.dto.LoginDto;
import br.com.rraminelli.moviesbattle.userauth.dto.UserTokenDto;
import br.com.rraminelli.moviesbattle.userauth.entity.User;
import br.com.rraminelli.moviesbattle.userauth.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation("Get current current user info")
    @GetMapping("/current-user")
    public ResponseEntity<User> currentUser() {
        final String username = JwtSecurityUtils.getCurrentUsername();
        final User user = userService.findByUsername(username).get();
        return ResponseEntity.ok(user);
    }

}
