package br.com.rraminelli.moviesbattle.userauth.application.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public class JwtSecurityUtils {

    public static String getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof UserPrincipal) {
            return ((UserPrincipal) authentication.getDetails()).getUsername();
        } else if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return ((UserPrincipal) authentication.getPrincipal()).getUsername();
        } else if(authentication != null && authentication.getPrincipal() instanceof User) {
           return ((User) authentication.getPrincipal()).getUsername();
        } else if(authentication != null && authentication.getDetails() instanceof String) {
            authentication.getDetails().toString();
        }
        return null;
    }

}
