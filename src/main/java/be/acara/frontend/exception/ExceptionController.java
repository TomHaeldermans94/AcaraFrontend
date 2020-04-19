package be.acara.frontend.exception;

import feign.FeignException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

@ControllerAdvice
@Controller
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
    
    @GetMapping("/forbidden")
    public String forbiddenPage() {
        return "error/unauthorized";
    }
}
