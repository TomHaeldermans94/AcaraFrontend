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
    
    @ExceptionHandler(FeignException.class)
    public String handleFeignClientExceptions(FeignException fe) {
        switch (fe.status()) {
            case 401:
            case 403: return "error/unauthorized";
            case 404: return "error/notFound";
            default: return "error/genericError";
        }
    }
    
    @ExceptionHandler(Exception.class)
    public String genericErrorPage(Exception e) {
        return "error/genericError";
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public String unauthorized() {
        return "error/unauthorized";
    }
    
    @ExceptionHandler(NotFoundException.class)
    public String notFound(NotFoundException nfe) {
        return "error/notFound";
    }
    
    @GetMapping("/forbidden")
    public String forbiddenPage() {
        return "error/unauthorized";
    }
}
