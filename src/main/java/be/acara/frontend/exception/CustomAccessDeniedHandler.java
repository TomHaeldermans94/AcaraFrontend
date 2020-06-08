package be.acara.frontend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    
    /**
     * Redirects users (anonymous and logged in) to /forbidden if they access a protected resource without proper
     * clearance
     */
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null) {
            log.warn("User: {} attempted to access the protected URL: {}", authentication.getName(), httpServletRequest.getRequestURI());
        }
        
        httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/forbidden");
    }
}
