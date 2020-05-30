package be.acara.frontend.service;

import be.acara.frontend.controller.dto.CreateOrderDto;
import be.acara.frontend.domain.Cart;
import be.acara.frontend.model.CreateOrderModel;

public interface OrderService {
    /**
     * Creates an order
     * @param createOrderModel the object containing the order details
     */
    void create(CreateOrderModel createOrderModel);

    /**
     * Edit an order
     * @param id the id of the order to edit
     * @param createOrderDto the new body of the order
     */
    void edit(Long id, CreateOrderDto createOrderDto);

    /**
     * Removes an order by the given id
     * @param id the id given to remove
     */
    void remove(Long id);

    /**
     * The bulk version of {@link #create(CreateOrderModel)}
     * @param cart a cart containing one or more orders
     */
    void create(Cart cart);
}
