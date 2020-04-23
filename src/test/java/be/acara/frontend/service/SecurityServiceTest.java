package be.acara.frontend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {
    @InjectMocks
    private SecurityService securityService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private UserDetails userDetails;
    @Mock
    private UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken;
    
    @BeforeEach
    void setUp() {
    
    }
    
    @Test
    void findLoggedInUsername() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getDetails()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("admin");
        
        String answer = securityService.findLoggedInUsername();
        
        assertThat(answer).isEqualTo("admin");
        
        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(1)).getDetails();
        verify(userDetails, times(1)).getUsername();
    }
    
    @Test
    void findLoggedInUsername_notLoggedIn() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getDetails()).thenReturn(null);
    
        String answer = securityService.findLoggedInUsername();
    
        assertThat(answer).isNull();
        
        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(1)).getDetails();
        verify(userDetails, times(0)).getUsername();
    }
    
    @Test
    void autoLogin() {
        String username = "username";
        String password = "password";
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(usernamePasswordAuthenticationToken);
        
        securityService.autoLogin(username, password);
    }
}
