package be.acara.frontend.exception;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

@ControllerAdvice
@Controller
@Slf4j
public class ExceptionController {
    
    private static final String UNAUTHORIZED = "error/unauthorized";
    private static final String NOT_FOUND = "error/notFound";
    private static final String GENERIC_ERROR = "error/genericError";
    
    @ExceptionHandler(FeignException.class)
    public String handleFeignClientExceptions(FeignException fe) {
        switch (fe.status()) {
            case 401:
            case 403: return UNAUTHORIZED;
            case 404: return NOT_FOUND;
            default: return GENERIC_ERROR;
        }
    }
    
    @ExceptionHandler(Exception.class)
    public String genericErrorPage(Exception e) {
        log.error("Error", e);
        return GENERIC_ERROR;
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public String unauthorized() {
        return UNAUTHORIZED;
    }
    
    @ExceptionHandler(NotFoundException.class)
    public String notFound(NotFoundException nfe) {
        return NOT_FOUND;
    }
    
    @GetMapping("/forbidden")
    public String forbiddenPage() {
        return UNAUTHORIZED;
    }
}
