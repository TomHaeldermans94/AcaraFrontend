package be.acara.frontend.controller;

import be.acara.frontend.model.Event;
import be.acara.frontend.model.EventList;
import be.acara.frontend.service.EventFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Base64;

@Controller
@RequestMapping("/events")
public class EventController {
    
    private EventFeignClient eventFeignClient;

    @Autowired
    public EventController(EventFeignClient eventFeignClient) {
        this.eventFeignClient = eventFeignClient;
    }
    
    @GetMapping("/detail/{id}")
    public String displayEvent(@PathVariable("id") Long id, ModelMap model) {
        Event event = eventFeignClient.getEventById(id);
        model.addAttribute("event", event);
        model.addAttribute("eventImage", Base64.getEncoder().encodeToString(event.getImage()));
        return "eventDetails";
    }

    @GetMapping
    public String findAllEvents(ModelMap model) {
        EventList eventList = eventFeignClient.getEvents();
        model.addAttribute("events", eventList.getEventList());
        return "eventList";
    }
}
