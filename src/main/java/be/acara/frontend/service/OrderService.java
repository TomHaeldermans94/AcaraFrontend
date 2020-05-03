package be.acara.frontend.service;

import be.acara.frontend.controller.dto.CreateOrderDto;

public interface OrderService {

    void create(Long eventId);

    void edit(Long id, CreateOrderDto createOrderDto);

    void remove(Long id);

}
