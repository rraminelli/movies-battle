package br.com.rraminelli.moviesbattle.userauth.controller;

import br.com.rraminelli.moviesbattle.userauth.application.security.TokenProvider;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@RestController
@RequestMapping("/auth-server")
public class AuthUserPrincipalController {

    @Autowired
    private TokenProvider tokenProvider;

    @ApiOperation("URL for oauth2-resource")
    @GetMapping
    public Principal user(Principal user, HttpServletRequest request) {
        if (!tokenProvider.validateToken(request)) {
            throw new InvalidBearerTokenException("Invalid Token");
        }
        return user;
    }

}
