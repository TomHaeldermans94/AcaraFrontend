package be.acara.frontend.controller;

import be.acara.frontend.controller.dto.EventDto;
import be.acara.frontend.model.EventModel;
import be.acara.frontend.model.EventModelList;
import be.acara.frontend.service.EventService;
import be.acara.frontend.service.UserService;
import be.acara.frontend.service.mapper.EventMapper;
import be.acara.frontend.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/events")
public class EventController {
    private final EventMapper mapper;
    private final EventService eventService;
    private final UserService userService;

    private static final String REDIRECT_EVENTS = "redirect:/events";
    private static final String ATTRIBUTE_EVENT = "event";
    private static final String ATTRIBUTE_EVENTS = "events";
    private static final String ATTRIBUTE_LIKED_EVENTS = "likedEvents";
    private static final String ATTRIBUTE_EVENT_IMAGE = "eventImage";


    @Autowired
    public EventController(EventMapper mapper, EventService eventService, UserService userService) {
        this.mapper = mapper;
        this.eventService = eventService;
        this.userService = userService;
    }

    @GetMapping("/detail/{id}")
    public String displayEvent(@PathVariable("id") Long id, ModelMap model) {
        EventModel event = mapper.eventDtoToEventModel(eventService.getEvent(id));
        model.addAttribute(ATTRIBUTE_EVENT, event);
        model.addAttribute(ATTRIBUTE_EVENT_IMAGE, ImageUtil.convertToBase64(event.getImage()));
        return "eventDetails";
    }

    @GetMapping
    public String findAllEvents(Model model,
                                @RequestParam(name = "page", defaultValue = "1", required = false) int page,
                                @RequestParam(name = "size", defaultValue = "20", required = false) int size,
                                @RequestParam(name = "sort", defaultValue = "eventDate", required = false) String sort) {
        addCategories(model);
        if ("UNSORTED".equals(sort)) {
            sort="";
        }
        EventModelList eventModelList = mapper.eventDtoListToEventModelList(eventService.findAllEvents(page - 1, size < 1 ? 1 : size, sort));
        int totalPages = eventModelList.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken) {
            Long id = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
            EventModelList likedEventModelList = mapper.eventDtoListToEventModelList(eventService.getEventsThatUserLiked(id, page - 1, size < 1 ? 1 : size));
            model.addAttribute(ATTRIBUTE_LIKED_EVENTS, likedEventModelList);
        }
        model.addAttribute(ATTRIBUTE_EVENTS, eventModelList);
        return "eventList";
    }

    @GetMapping("/new")
    public String displayAddEventForm(Model model) {
        model.addAttribute(ATTRIBUTE_EVENT, new EventModel());
        addCategories(model);
        return "addEvent";
    }

    @PostMapping("/new")
    public String handleAddEventForm(@Valid @ModelAttribute(ATTRIBUTE_EVENT) EventModel event, BindingResult br, @RequestParam(ATTRIBUTE_EVENT_IMAGE) MultipartFile eventImage, Model model) throws IOException {
        if (br.hasErrors()) {
            addCategories(model);
            return "addEvent";
        }
        event.setImage(eventImage.getBytes());
        eventService.addEvent(mapper.eventModelToEventDto(event));
        return REDIRECT_EVENTS;
    }

    @GetMapping("/{id}")
    public String displayEditEventForm(@PathVariable("id") Long id, Model model) {
        EventModel event = mapper.eventDtoToEventModel(eventService.getEvent(id));
        model.addAttribute(ATTRIBUTE_EVENT, event);
        model.addAttribute(ATTRIBUTE_EVENT_IMAGE, ImageUtil.convertToBase64(event.getImage()));
        addCategories(model);
        return "editEvent";
    }

    @PostMapping("/{id}")
    public String handleEditEventForm(@Valid @ModelAttribute(ATTRIBUTE_EVENT) EventModel event, BindingResult br, @RequestParam(ATTRIBUTE_EVENT_IMAGE) MultipartFile eventImage, Model model) throws IOException {
        EventDto eventDtoFromDb = eventService.getEvent(event.getId());
        EventModel eventFromDb = mapper.eventDtoToEventModel(eventDtoFromDb);
        if (br.hasErrors()) {
            addCategories(model);
            model.addAttribute(ATTRIBUTE_EVENT, event);
            model.addAttribute(ATTRIBUTE_EVENT_IMAGE, ImageUtil.convertToBase64(eventDtoFromDb.getImage()));
            return "editEvent";
        }
        event.setImage(eventImage.getBytes());
        eventService.editEvent(eventFromDb.getId(), mapper.eventModelToEventDto(event));
        return REDIRECT_EVENTS;
    }

    @GetMapping("/search")
    public String getSearchForm(Model model, @RequestParam Map<String, String> params) {
        params.entrySet().removeIf(e -> e.getValue().isEmpty()); //remove empty values from the set to avoid errors when parsing dates or bigDecimals
        if (params.isEmpty()) {
            return "redirect:";
        }
        EventModelList searchResults = mapper.eventDtoListToEventModelList(eventService.search(params));
        model.addAttribute(ATTRIBUTE_EVENTS, searchResults);
        return "eventList";
    }

    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable("id") Long id) {
        eventService.delete(id);
        return REDIRECT_EVENTS;
    }


    private void addCategories(Model model) {
        model.addAttribute("categoryList", eventService.getCategories());
    }
}
