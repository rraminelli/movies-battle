package br.com.rraminelli.moviesbattle.userauth.application.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Slf4j
@Component
public class TokenProvider {

    @Value("${app.auth.tokenSecret}")
    private String tokenSecret;
    @Value("${app.auth.tokenExpiration}")
    private Long tokenExpiration;

    public String createToken(Authentication authentication) {
        final UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return createToken(userPrincipal.getUsername());
    }

    public String createToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + tokenExpiration * 1000))
                .signWith(SignatureAlgorithm.HS512, tokenSecret)
                .compact();
    }

    public String getUserUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(tokenSecret)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        }
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

    public boolean validateToken(HttpServletRequest request) {
        final String token = getJwtFromRequest(request);
        return !StringUtils.hasText(token) || validateToken(token);
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(tokenSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            log.error("Error JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Error JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Error - Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Error - Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("Error - JWT claims is empty.");
        }
        return false;
    }

}
