package br.com.rraminelli.moviesbattle.userauth.controller;

import br.com.rraminelli.moviesbattle.userauth.application.security.JwtUserAuthenticationManager;
import br.com.rraminelli.moviesbattle.userauth.dto.LoginDto;
import br.com.rraminelli.moviesbattle.userauth.dto.UserTokenDto;
import br.com.rraminelli.moviesbattle.userauth.entity.User;
import br.com.rraminelli.moviesbattle.userauth.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtUserAuthenticationManager jwtUserAuthenticationService;
    @Autowired
    private UserService userService;

    @ApiOperation(value = "Login user. Get a JWT token")
    @PostMapping("/login")
    public ResponseEntity<UserTokenDto> login(@Valid @RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(jwtUserAuthenticationService.login(loginDto.getUsername(), loginDto.getPassword()));
    }

    @ApiOperation("Register a new user. Get a JWT token")
    @PostMapping("/signup")
    public ResponseEntity<UserTokenDto> signup(@Valid @RequestBody User user) {
        final String password = user.getPassword();
        userService.create(user);
        return login(new LoginDto(user.getUsername(), password));
    }

    @ApiOperation("Refresh token. Get a new JWT token")
    @PostMapping("/refresh-token")
    public ResponseEntity<UserTokenDto> refreshToken(HttpServletRequest request) {
        return ResponseEntity.ok(jwtUserAuthenticationService.refreshToken(request));
    }

}
