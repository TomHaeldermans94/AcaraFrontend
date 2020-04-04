package be.acara.frontend.security;

import be.acara.frontend.security.domain.JwtToken;
import be.acara.frontend.security.service.JwtTokenService;
import be.acara.frontend.security.service.SecurityService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class FeignClientInterceptor implements RequestInterceptor {
    @Autowired
    private JwtTokenService tokenService;
    @Autowired
    private SecurityService securityService;
    
    @Override
    public void apply(RequestTemplate requestTemplate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null) {
            return;
        }
    
        String name = authentication.getName();
        JwtToken token = tokenService.getToken(name);
        if (token != null) {
            requestTemplate.header("Authorization", token.getToken());
        }
    }
}
