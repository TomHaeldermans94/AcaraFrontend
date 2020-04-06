package be.acara.frontend.security;

import be.acara.frontend.domain.JwtToken;
import be.acara.frontend.service.JwtTokenService;
import com.auth0.jwt.exceptions.TokenExpiredException;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class FeignClientInterceptor implements RequestInterceptor {
    @Autowired
    private JwtTokenService tokenService;
    
    @Override
    public void apply(RequestTemplate requestTemplate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return;
        }
    
        String name = authentication.getName();
        JwtToken token = tokenService.getToken(name);
        if (token != null && token.getExpirationDate().after(new Date(System.currentTimeMillis()))) {
            requestTemplate.header("Authorization", "Bearer " + token.getToken());
        } else if (token != null && token.getExpirationDate().before(new Date(System.currentTimeMillis()))) {
            throw new TokenExpiredException("Token has expired");
        }
    }
}
