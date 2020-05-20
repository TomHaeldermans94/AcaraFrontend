package be.acara.frontend.security;

import be.acara.frontend.service.JwtTokenService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class TokenLogoutHandler extends SimpleUrlLogoutSuccessHandler {
    
    private final JwtTokenService jwtTokenService;
    
    public TokenLogoutHandler(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }
    
    
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication != null) {
            jwtTokenService.remove(authentication.getName());
        }
        
        response.sendRedirect(request.getContextPath() + "/events");
        
        super.onLogoutSuccess(request, response, authentication);
    }
}
