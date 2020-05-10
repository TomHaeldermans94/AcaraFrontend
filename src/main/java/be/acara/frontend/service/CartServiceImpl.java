package be.acara.frontend.service;

import be.acara.frontend.domain.Cart;
import be.acara.frontend.domain.User;
import be.acara.frontend.repository.CartRepository;
import org.springframework.stereotype.Service;

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
}
