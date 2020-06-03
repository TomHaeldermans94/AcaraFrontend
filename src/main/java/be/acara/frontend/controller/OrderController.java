package be.acara.frontend.controller;

import be.acara.frontend.model.CreateOrderModel;
import be.acara.frontend.service.CartService;
import be.acara.frontend.service.EventService;
import be.acara.frontend.service.OrderService;
import be.acara.frontend.service.mapper.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final EventMapper mapper;
    private final EventService eventService;
    private final CartService cartService;

    private static final String ATTRIBUTE_CREATE_ORDER_MODEL = "createOrderModel";

    @Autowired
    public OrderController(OrderService orderService, EventMapper mapper, EventService eventService, CartService cartService) {
        this.orderService = orderService;
        this.mapper = mapper;
        this.eventService = eventService;
        this.cartService = cartService;
    }

    @PostMapping("/new")
    public String createOrder(@ModelAttribute("createOrderModel") CreateOrderModel createOrderModel) {
        cartService.addToCart(createOrderModel);
        return "redirect:/events";
    }
    
    @PostMapping("/payment")
    public String handlePayment() {
        orderService.create(cartService.getCart());
        cartService.clearCart();
        return "redirect:/events";
    }
    
    @GetMapping("/new/{eventId}")
    public String displayOrderForm(@PathVariable("eventId") Long eventId, Model model) {
        model.addAttribute(
                ATTRIBUTE_CREATE_ORDER_MODEL,
                new CreateOrderModel(mapper.eventDtoToEventModel(eventService.getEvent(eventId)), 1)
        );
        return "buyOrder";
    }
    
    @GetMapping(value = "/ticket/{eventId}", produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> downloadEventTicket(@PathVariable("eventId") Long eventId, HttpServletResponse response) {
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=acara_events_ticket.pdf")
                .body(orderService.getEventTicket(eventId).getTicket());
    }
}
