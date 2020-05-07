package be.acara.frontend.controller;

import be.acara.frontend.model.CreateOrderModel;
import be.acara.frontend.service.EventService;
import be.acara.frontend.service.OrderService;
import be.acara.frontend.service.mapper.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final EventMapper mapper;
    private final EventService eventService;

    private static final String ATTRIBUTE_CREATE_ORDER_MODEL = "createOrderModel";

    @Autowired
    public OrderController(OrderService orderService, EventMapper mapper, EventService eventService) {
        this.orderService = orderService;
        this.mapper = mapper;
        this.eventService = eventService;
    }

    @PostMapping("/new")
    public String createOrder(@ModelAttribute("createOrderModel") CreateOrderModel createOrderModel) {
        orderService.create(createOrderModel);
        return "redirect:/events";
    }

    @GetMapping("/new/{eventId}")
    public String displayOrderForm(@PathVariable("eventId") Long eventId, ModelMap model) {
        model.addAttribute(
                ATTRIBUTE_CREATE_ORDER_MODEL,
                new CreateOrderModel(mapper.eventDtoToEventModel(eventService.getEvent(eventId)), 1)
        );
        return "buyOrder";
    }
}
