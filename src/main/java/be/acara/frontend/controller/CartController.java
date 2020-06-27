package be.acara.frontend.controller;

import be.acara.frontend.model.CreateOrderModel;
import be.acara.frontend.service.CartService;
import be.acara.frontend.service.mapper.CartMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cart")
public class CartController {
    
    private final CartService cartService;
    private final CartMapper cartMapper;
    
    private static final String CART_LOCATION = "cart/cart";
    
    public CartController(CartService cartService, CartMapper cartMapper) {
        this.cartService = cartService;
        this.cartMapper = cartMapper;
    }
    
    /**
     * Displays the cart of the currently logged in user.
     *
     * @param model the model to attach the cart to
     * @return a string containing the location of the html to render
     */
    @GetMapping
    public String getCurrentCart(Model model) {
        model.addAttribute(cartMapper.cartToCartModel(cartService.getCart()));
        return CART_LOCATION;
    }
    
    /**
     * Handles the addition of an order to the currently logged in user's cart.
     *
     * @param createOrderModel the order details
     * @param model            the model to add the cart to
     * @return a string containing the location of the html to render
     */
    @PostMapping
    public String addEvent(CreateOrderModel createOrderModel, Model model) {
        model.addAttribute(cartMapper.cartToCartModel(cartService.addToCart(createOrderModel)));
        return CART_LOCATION;
    }
    
    /**
     * Clears the cart of the current logged in user
     *
     * @param model the model to add the cart to
     * @return a string containing the location of the html to render
     */
    @GetMapping("/clear")
    public String clearCart(Model model) {
        cartService.clearCart();
        model.addAttribute(cartMapper.cartToCartModel(cartService.getCart()));
        return CART_LOCATION;
    }
}
