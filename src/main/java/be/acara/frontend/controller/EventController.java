package be.acara.frontend.controller;

import be.acara.frontend.model.Event;
import be.acara.frontend.model.EventList;
import be.acara.frontend.service.EventFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    @GetMapping("/new")
    public String displayEventForm(Model model){
        model.addAttribute("categoryList", eventFeignClient.getAllCategories().getCategories());
        model.addAttribute("event", new Event());
        return "addEventForm";
    }

    @PostMapping("/new")
    public String handleForm(@Valid @ModelAttribute("event") Event event, BindingResult br) {
        if (br.hasErrors()){
            return "addEventForm";
        }
        eventFeignClient.addEvent(event);
        return "redirect:/";
    }
}
