package be.acara.frontend.service;

import be.acara.frontend.controller.dto.CreateOrderDto;
import be.acara.frontend.domain.Cart;
import be.acara.frontend.model.CreateOrderModel;

public interface OrderService {
    
    void create(CreateOrderModel createOrderModel);
    
    void edit(Long id, CreateOrderDto createOrderDto);

    void remove(Long id);
    
    void create(Cart cart);
}
