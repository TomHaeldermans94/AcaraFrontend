package be.acara.frontend.controller;

import be.acara.frontend.controller.dto.EventDto;
import be.acara.frontend.controller.dto.EventDtoList;
import be.acara.frontend.model.Event;
import be.acara.frontend.model.EventList;
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
        EventDto eventDto = eventFeignClient.getEventById(id);
        Event event = mapper.map(eventDto);
        model.addAttribute("event", event);
        if (event.getImage() != null) {
            model.addAttribute("eventImage", Base64.getEncoder().encodeToString(event.getImage()));
        }
        return "eventDetails";
    }

    @GetMapping
    public String findAllEvents(ModelMap model) {
        EventDtoList eventDtoList = eventFeignClient.getEvents();
        EventList eventList = mapper.map(eventDtoList);
        model.addAttribute("events", eventList.getEventList());
        return "eventList";
    }

    @GetMapping("/new")
    public String displayAddEventForm(Model model) {
        model.addAttribute("event", new Event());
        return "addEvent";
    }

    @PostMapping("/new")
    public ModelAndView handleAddEventForm(@Valid @ModelAttribute("event") Event event, BindingResult br, @RequestParam("eventImage") MultipartFile eventImage) {
        if (br.hasErrors()) {
            return new ModelAndView("addEvent");
        }
        if (eventImage != null) {
            try {
                event.setImage(eventImage.getBytes());
            } catch (IOException e) {
                logger.error("Something went wrong when setting the image to the event");
            }
        }
        eventFeignClient.addEvent(mapper.map(event));
        return new ModelAndView("redirect:/events");
    }

    @GetMapping("/{id}")
    public String displayEditEventForm(@PathVariable("id") Long id, Model model) {
        EventDto eventDto = eventFeignClient.getEventById(id);
        Event event = mapper.map(eventDto);
        model.addAttribute("event", event);
        if (event.getImage() != null) {
            model.addAttribute("eventImage", Base64.getEncoder().encodeToString(event.getImage()));
        }
        return "editEvent";
    }

    @PostMapping("/{id}")
    public ModelAndView handleEditEventForm(@Valid @ModelAttribute("event") Event event, BindingResult br, @RequestParam("eventImage") MultipartFile eventImage) {
        EventDto eventDtoFromDb = eventFeignClient.getEventById(event.getId());
        Event eventFromDb = mapper.map(eventDtoFromDb);
        if (br.hasErrors()) {
            return showNewModelAndViewInCaseOfErrorsInEditEvent(eventFromDb);
        }
        if (eventImage != null) {
            try {
                event.setImage(eventImage.getBytes());
            } catch (IOException e) {
                logger.error("Something went wrong when setting the image to the event");
            }
        }
        eventFeignClient.editEvent(eventFromDb.getId(), mapper.map(event));
        return new ModelAndView("redirect:/events");
    }

    private ModelAndView showNewModelAndViewInCaseOfErrorsInEditEvent(Event event) {
        if (event.getImage() != null) {
            String imageString = Base64.getEncoder().encodeToString(event.getImage());
            return new ModelAndView("editEvent", "eventImage", imageString);
        }
        return new ModelAndView("editEvent");
    }

    @GetMapping("/search")
    public String getSearchForm(Model model, @RequestParam Map<String, String> params) {
        if (!params.isEmpty()) {
            params.entrySet().removeIf(e -> e.getValue().isEmpty()); //remove empty values from the set to avoid errors when parcing dates or bigDecimals
            EventDtoList searchResults = eventFeignClient.search(params);
            model.addAttribute("events", mapper.map(searchResults).getEventList());
            return "eventList";
        }
        return "searchForm";
    }

    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable("id") Long id){
        eventFeignClient.deleteEvent(id);
        return "redirect:/events";
    }
    
    @ModelAttribute(name = "categoryList")
    public List<String> getCategories() {
        return eventFeignClient.getAllCategories().getCategories();
    }
}
