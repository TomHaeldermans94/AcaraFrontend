package be.acara.frontend.controller;

import be.acara.frontend.model.EventModel;
import be.acara.frontend.service.EventService;
import be.acara.frontend.service.OrderService;
import be.acara.frontend.service.mapper.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final EventMapper mapper;
    private final EventService eventService;

    private static final String ATTRIBUTE_EVENT = "event";

    @Autowired
    public OrderController(OrderService orderService, EventMapper mapper, EventService eventService) {
        this.orderService = orderService;
        this.mapper = mapper;
        this.eventService = eventService;
    }

    @PostMapping()
    public String createOrder(@RequestParam(name = "event", required = true) Long eventId) {
        orderService.create(eventId);
        return "redirect:/events";
    }

    @GetMapping()
    public String displayOrderForm(@RequestParam(name = "event", required = true) Long eventId, ModelMap model) {
        EventModel event = mapper.eventDtoToEventModel(eventService.getEvent(eventId));
        model.addAttribute(ATTRIBUTE_EVENT, event);
        return "buyOrder";
    }
}
