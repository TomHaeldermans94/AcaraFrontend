package be.acara.frontend.service;

import be.acara.frontend.controller.dto.EventDto;
import be.acara.frontend.domain.Cart;
import be.acara.frontend.domain.CartItem;
import be.acara.frontend.domain.User;
import be.acara.frontend.model.CreateOrderModel;
import be.acara.frontend.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CartServiceImpl implements CartService {
    
    private final CartRepository cartRepository;
    private final UserService userService;
    private final EventService eventService;
    
    public CartServiceImpl(CartRepository cartRepository, UserService userService, EventService eventService) {
        this.cartRepository = cartRepository;
        this.userService = userService;
        this.eventService = eventService;
    }
    
    @Override
    public Cart getCart() {
        User user = userService.getCurrentUser();
        return cartRepository.findByUserUsername(user.getUsername())
                .orElse(new Cart(user));
    }
    
    @Override
    public Cart addToCart(CreateOrderModel createOrderModel) {
        Cart cart = getCart();
        EventDto event = eventService.getEvent(createOrderModel.getEventModel().getId());
    
        CartItem cartItem = CartItem.builder()
                .amount(createOrderModel.getAmountOfTickets())
                .eventId(event.getId())
                .itemTotal(event.getPrice().multiply(new BigDecimal(createOrderModel.getAmountOfTickets())))
                .cart(cart)
                .build();
    
        cart.setTotal(cart.getTotal().add(cartItem.getItemTotal()));
        cart.getItems().add(cartItem);
        
        cartRepository.saveAndFlush(cart);
        return cart;
    }
}
