package be.acara.frontend.controller;

import be.acara.frontend.model.Event;
import be.acara.frontend.service.EventFeignClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Base64;

@Controller
@RequestMapping("/event")
public class EventController {
    
    private EventFeignClient eventFeignClient;
    
    public EventController(EventFeignClient eventFeignClient) {
        this.eventFeignClient = eventFeignClient;
    }
    
    @GetMapping("/{id}")
    public String displayEvent(@PathVariable("id") Long id, ModelMap model) {
        Event event = eventFeignClient.getEventById(id);
        model.addAttribute("event", event);
        model.addAttribute("eventImage", Base64.getEncoder().encodeToString(event.getImage()));
        return "eventDetails";
    }
}
