package br.com.rraminelli.moviesbattle.userauth.application.security;

import br.com.rraminelli.moviesbattle.userauth.dto.UserTokenDto;
import br.com.rraminelli.moviesbattle.userauth.entity.User;
import br.com.rraminelli.moviesbattle.userauth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Component
public class JwtUserAuthenticationManager {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    public UserTokenDto login(String username, String password) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return getUserTokenDto(authentication);
    }

    public UserTokenDto refreshToken(HttpServletRequest request) {
        Optional<String> token = Optional.ofNullable(tokenProvider.getJwtFromRequest(request));
        if (!token.isPresent() || !tokenProvider.validateToken(token.get())) {
            throw new BadCredentialsException("Invalid Token or not found.");
        } else {
            final String username = tokenProvider.getUserUsernameFromToken(token.get());
            return this.createNewToken(request, username);
        }
    }

    public UserTokenDto createNewToken(HttpServletRequest request, String username) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return getUserTokenDto(authentication);
    }

    private UserTokenDto getUserTokenDto(Authentication authentication) {
        final String username = ((UserPrincipal) authentication.getPrincipal()).getUsername();
        final Optional<User> user = userService.findByUsername(username);
        UserTokenDto userTokenDto = new UserTokenDto();
        userTokenDto.setUsername(username);
        userTokenDto.setToken(tokenProvider.createToken(authentication));
        userTokenDto.setName(user.get().getName());
        return userTokenDto;
    }

}
