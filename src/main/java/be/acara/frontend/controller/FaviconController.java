package be.acara.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
class FaviconController {
  
    @GetMapping("favicon.ico")
    @ResponseBody
    public void returnNoFavicon() {
        // this method so that any favicon.ico calls wont throw an error
    }
}
