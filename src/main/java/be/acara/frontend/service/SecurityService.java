package be.acara.frontend.service;

import be.acara.frontend.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SecurityService {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    
    public SecurityService(AuthenticationManager authenticationManager, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Retrieves the username through the SecurityContext
     * @return the username of the logged in user, else return null
     */
    public String findLoggedInUsername() {
        Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
        if (details instanceof UserDetails) {
            return ((UserDetails) details).getUsername();
        }
        return null;
    }

    /**
     * Method to automatically login the user
     * @param username the username of the user
     * @param password the password of the user
     */
    public void autoLogin(String username, String password) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        authenticationManager.authenticate(authenticationToken);
        
        if (authenticationToken.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            log.debug("Autologin {} success", username);
        }
    }

    /**
     * Checks whether the authentication id matches the given userId
     * @param authentication the Spring authentication object
     * @param userId the ID to check against
     * @return true if it matches, otherwise false
     */
    public boolean hasUserId(Authentication authentication, Long userId) {
        if (authentication.getPrincipal() instanceof User) {
            return ((User) authentication.getPrincipal()).getId().equals(userId);
        }
        return false;
    }
}
