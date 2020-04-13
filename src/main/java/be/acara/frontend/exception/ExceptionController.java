package be.acara.frontend.exception;

import feign.FeignException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {
    
    @ExceptionHandler(FeignException.class)
    public String handleFeignClientExceptions(FeignException fe) {
        return "redirect:/login";
    }
    
}
