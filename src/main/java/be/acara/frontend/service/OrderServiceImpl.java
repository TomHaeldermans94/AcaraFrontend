package be.acara.frontend.service;

import be.acara.frontend.controller.dto.CreateOrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {


    private final OrderFeignClient orderFeignClient;

    @Autowired
    public OrderServiceImpl(OrderFeignClient orderFeignClient) {
        this.orderFeignClient = orderFeignClient;
    }


    @Override
    public void create(Long eventId) {
        orderFeignClient.create(CreateOrderDto.builder()
                .eventId(eventId)
                .amountOfTickets(1)
                .build());
    }

    @Override
    public void edit(Long id, CreateOrderDto createOrderDto) {
        orderFeignClient.edit(id, createOrderDto);
    }

    @Override
    public void remove(Long id) {
        orderFeignClient.remove(id);
    }

    @Override
    public CreateOrderDto findById(Long id) {
        return null;
    }

    @Override
    public Page<CreateOrderDto> getAllOrders(Pageable pageable) {
        return null;
    }
}
