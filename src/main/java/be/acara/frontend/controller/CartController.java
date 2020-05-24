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
    
    public CartController(CartService cartService, CartMapper cartMapper) {
        this.cartService = cartService;
        this.cartMapper = cartMapper;
    }
    
    @GetMapping
    public String getCurrentCart(Model model) {
        model.addAttribute(cartMapper.cartToCartModel(cartService.getCart()));
        return "cart/cart";
    }
    
    @PostMapping
    public String addEvent(CreateOrderModel createOrderModel, Model model) {
        model.addAttribute(cartMapper.cartToCartModel(cartService.addToCart(createOrderModel)));
        return "cart/cart";
    }
    
    @GetMapping("/clear")
    public String clearCart(Model model) {
        cartService.clearCart();
        model.addAttribute(cartMapper.cartToCartModel(cartService.getCart()));
        return "cart/cart";
    }
}
