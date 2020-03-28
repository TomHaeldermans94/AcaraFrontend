package be.acara.frontend.controller;

import be.acara.frontend.model.Event;
import be.acara.frontend.model.EventList;
import be.acara.frontend.model.EventWithoutImage;
import be.acara.frontend.service.EventFeignClient;
import be.acara.frontend.service.mapper.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/events")
public class EventController {

    private final EventFeignClient eventFeignClient;
    private final EventMapper mapper;

    @Autowired
    public EventController(EventFeignClient eventFeignClient, EventMapper mapper) {
        this.eventFeignClient = eventFeignClient;
        this.mapper = mapper;
    }

    @GetMapping("/detail/{id}")
    public String displayEvent(@PathVariable("id") Long id, ModelMap model) {
        Event event = eventFeignClient.getEventById(id);
        model.addAttribute("event", event);
        if (event.getImage() != null) {
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
    public String displayAddEventForm(Model model) {
        model.addAttribute("event", new Event());
        return "addEvent";
    }

    @PostMapping("/new")
    public ModelAndView handleAddEventForm(@Valid @ModelAttribute("event") EventWithoutImage eventWithoutImage, BindingResult br, @RequestParam("image") MultipartFile image) {
        Event eventFromDb = eventFeignClient.getEventById(eventWithoutImage.getId());
        if (br.hasErrors()) {
            return showNewModelAndViewInCaseOfErrorsInAddOrEditEvents(eventFromDb,"addEvent");
        }
        eventFeignClient.addEvent(mapper.mapEventWithoutImageToEventWithMultipartImage(eventWithoutImage, image));
        return new ModelAndView("redirect:/events");
    }

    @GetMapping("/{id}")
    public String displayEditEventForm(@PathVariable("id") Long id, Model model) {
        Event event = eventFeignClient.getEventById(id);
        model.addAttribute("event", event);
        if (event.getImage() != null) {
            model.addAttribute("eventImage", Base64.getEncoder().encodeToString(event.getImage()));
        }
        return "editEvent";
    }

    @PostMapping("/{id}")
    public ModelAndView handleEditEventForm(@Valid @ModelAttribute("event") EventWithoutImage eventWithoutImage, BindingResult br, @RequestParam("image") MultipartFile image) {
        Event eventFromDb = eventFeignClient.getEventById(eventWithoutImage.getId());
        if (br.hasErrors()) {
            return showNewModelAndViewInCaseOfErrorsInAddOrEditEvents(eventFromDb, "editEvent");
        }
        if (image.getSize() != 0) {
            eventFromDb = mapper.mapEventWithoutImageToEventWithMultipartImage(eventWithoutImage, image);
        } else {
            eventFromDb = mapper.mapEventWithoutImageToEventWithUnchangedImage(eventWithoutImage, eventFromDb.getImage());
        }
        eventFeignClient.editEvent(eventFromDb.getId(), eventFromDb);
        return new ModelAndView("redirect:/events");
    }

    private ModelAndView showNewModelAndViewInCaseOfErrorsInAddOrEditEvents(Event event, String viewName) {
        if (event.getImage() != null) {
            String imageString = Base64.getEncoder().encodeToString(event.getImage());
            return new ModelAndView(viewName, "eventImage", imageString);
        }
        return new ModelAndView(viewName);
    }

    @GetMapping("/search")
    public String getSearchForm(Model model, @RequestParam Map<String, String> params) {
        if (!params.isEmpty()) {
            params.entrySet().removeIf(e -> e.getValue().isEmpty()); //remove empty values from the set to avoid errors when parcing dates or bigDecimals
            EventList searchResults = eventFeignClient.search(params);
            model.addAttribute("events", searchResults.getEventList());
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
