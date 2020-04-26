package be.acara.frontend.service;

import be.acara.frontend.controller.dto.CreateOrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    void create(Long eventId);

    void edit(Long id, CreateOrderDto createOrderDto);

    void remove(Long id);

    CreateOrderDto findById(Long id);

    Page<CreateOrderDto> getAllOrders(Pageable pageable);
}
