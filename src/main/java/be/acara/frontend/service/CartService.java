package be.acara.frontend.service;

import be.acara.frontend.domain.Cart;
import be.acara.frontend.model.CreateOrderModel;

public interface CartService {
    Cart getCart();
    Cart addToCart(CreateOrderModel createOrderModel);
    
    void clearCart();
}
