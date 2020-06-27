package be.acara.frontend.service;

import be.acara.frontend.domain.Cart;
import be.acara.frontend.model.CreateOrderModel;

public interface CartService {
    /**
     * Gets the cart of the current user
     * @return the cart of the current user
     */
    Cart getCart();

    /**
     * Adds an order to the cart
     * @param createOrderModel the order model containing all relevant order details
     * @return the updated cart of the current user
     */
    Cart addToCart(CreateOrderModel createOrderModel);

    /**
     * Clears the current cart's contents
     */
    void clearCart();
}
