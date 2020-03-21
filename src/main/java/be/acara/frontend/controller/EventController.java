package be.acara.frontend.controller;

import be.acara.frontend.model.Event;
import be.acara.frontend.model.EventList;
import be.acara.frontend.model.EventWithoutImage;
import be.acara.frontend.service.EventFeignClient;
import be.acara.frontend.service.mapper.EventMapper;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/events")
public class EventController {
    
    private final EventFeignClient eventFeignClient;
    private final EventMapper mapper;
    private Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    public EventController(EventFeignClient eventFeignClient, EventMapper mapper) {
        this.eventFeignClient = eventFeignClient;
        this.mapper = mapper;
    }
    
    @GetMapping("/detail/{id}")
    public String displayEvent(@PathVariable("id") Long id, ModelMap model) {
        Event event = eventFeignClient.getEventById(id);
        model.addAttribute("event", event);
        if(event.getImage() != null) {
            model.addAttribute("eventImage", Base64.getEncoder().encodeToString(event.getImage()));
        }
        return "eventDetails";
    }

    @GetMapping
    public String findAllEvents(ModelMap model) {
        EventList eventList = eventFeignClient.getEvents();
        model.addAttribute("events", eventList.getEventList());
        return "eventList";
    }

    @GetMapping("/new")
    public String displayAddEventForm(Model model){
        model.addAttribute("event", new Event());
        return "addEvent";
    }

    @PostMapping("/new")
    public ModelAndView handleAddEventForm(@Valid @ModelAttribute("event") EventWithoutImage eventWithoutImage, BindingResult br, @RequestParam("image") MultipartFile image) {
        Event eventFromDb = eventFeignClient.getEventById(eventWithoutImage.getId());
        if (br.hasErrors()){
            return showNewModelAndViewInCaseOfErrorsInAddOrEditEvents(eventFromDb, image, "addEvent");
        }
        mapper.mapEventWithoutImageToEventWithMultipartImage(eventWithoutImage, image);
        return new ModelAndView("redirect:/events");
    }

    @GetMapping("/{id}")
    public String displayEditEventForm(@PathVariable("id") long id, Model model){
        Event event = eventFeignClient.getEventById(id);
        model.addAttribute("event", event);
        if(event.getImage() != null) {
            model.addAttribute("eventImage", Base64.getEncoder().encodeToString(event.getImage()));
        }
        return "editEvent";
    }

    @PostMapping("/{id}")
    public ModelAndView handleEditEventForm(@Valid @ModelAttribute("event") EventWithoutImage eventWithoutImage, BindingResult br, @RequestParam("image") MultipartFile image) {
        Event eventFromDb = eventFeignClient.getEventById(eventWithoutImage.getId());
        if (br.hasErrors()){
            return showNewModelAndViewInCaseOfErrorsInAddOrEditEvents(eventFromDb, image, "editEvent");
        }
        Event event = mapper.mapEventWithoutImageToEventWithMultipartImage(eventWithoutImage, image);
        eventFeignClient.editEvent(event.getId(), event);
        return new ModelAndView("redirect:/events");
    }

    private ModelAndView showNewModelAndViewInCaseOfErrorsInAddOrEditEvents(Event event, MultipartFile image, String viewName){
        try {
            if(image.getSize() != 0) {
                String imageString = Base64.getEncoder().encodeToString(image.getBytes());
                return new ModelAndView(viewName, "eventImage", imageString);
            }
            else if (event.getImage() != null){
                String imageString = Base64.getEncoder().encodeToString(event.getImage());
                return new ModelAndView(viewName, "eventImage", imageString);
            }
        } catch (IOException e) {
            logger.error(String.format("Something went wrong when showing the new view for: %s", viewName));
        }
        return new ModelAndView(viewName);
    }
    
    @GetMapping("/search")
    public String getSearchForm(Model model, @RequestParam Map<String, String> params) {
        if (!params.isEmpty()) {
            EventList searchResults = eventFeignClient.search(params);
            model.addAttribute("events",searchResults.getEventList());
            return "eventList";
        }
        return "searchForm";
    }

    @ModelAttribute (name = "categoryList")
    public List<String> getCategories() {
        return eventFeignClient.getAllCategories().getCategories();
    }
}
