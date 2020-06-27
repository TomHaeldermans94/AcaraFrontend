package be.acara.frontend.security;

import be.acara.frontend.domain.JwtToken;
import be.acara.frontend.service.JwtTokenService;
import com.auth0.jwt.exceptions.TokenExpiredException;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeignClientInterceptorTest {
    @Mock
    private JwtTokenService jwtTokenService;
    @InjectMocks
    private FeignClientInterceptor feignClientInterceptor;
    
    private RequestTemplate requestTemplate;
    
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private JwtToken jwtToken;
    
    @BeforeEach
    void setUp() {
        requestTemplate = new RequestTemplate();
    }
    
    @Test
    void apply_withoutContext() {
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);
        feignClientInterceptor.apply(requestTemplate);
    
        assertThat(requestTemplate.headers()).doesNotContainKey("Authorization");
    }
    
    @Test
    void apply_withContext() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        
        when(authentication.getName()).thenReturn("");
        when(jwtTokenService.getToken(anyString())).thenReturn(jwtToken);
        when(jwtToken.getExpirationDate()).thenReturn(new Date(System.currentTimeMillis() + 10_000_000 ));
        
        feignClientInterceptor.apply(requestTemplate);
        
        assertThat(requestTemplate.headers()).containsKey("Authorization");
    }
    
    @Test
    void apply_withContext_expiredToken() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        
        when(authentication.getName()).thenReturn("");
        when(jwtTokenService.getToken(anyString())).thenReturn(jwtToken);
        when(jwtToken.getExpirationDate()).thenReturn(new Date(System.currentTimeMillis() - 10_000_000 ));
        
        assertThrows(TokenExpiredException.class, () -> feignClientInterceptor.apply(requestTemplate));
    }
}
