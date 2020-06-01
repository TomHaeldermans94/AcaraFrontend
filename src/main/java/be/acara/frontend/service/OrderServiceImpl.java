package be.acara.frontend.service;

import be.acara.frontend.controller.dto.CreateOrderDto;
import be.acara.frontend.controller.dto.CreateOrderList;
import be.acara.frontend.controller.dto.TicketDto;
import be.acara.frontend.domain.Cart;
import be.acara.frontend.model.CreateOrderModel;
import be.acara.frontend.service.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderFeignClient orderFeignClient;

    @Autowired
    public OrderServiceImpl(OrderMapper orderMapper, OrderFeignClient orderFeignClient) {
        this.orderMapper = orderMapper;
        this.orderFeignClient = orderFeignClient;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void create(CreateOrderModel createOrderModel) {
        orderFeignClient.create(
                orderMapper.createOrderModelToCreateOrderDto(createOrderModel)
        );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void create(Cart cart) {
        Set<CreateOrderDto> createOrderDtos = cart.getItems().stream().map(cartItem -> new CreateOrderDto(cartItem.getEventId(), cartItem.getAmount())).collect(Collectors.toSet());
        orderFeignClient.create(new CreateOrderList(createOrderDtos));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void edit(Long id, CreateOrderDto createOrderDto) {
        orderFeignClient.edit(id, createOrderDto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(Long id) {
        orderFeignClient.remove(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TicketDto getEventTicket(Long eventId) {
        return orderFeignClient.getEventTicket(eventId);
    }

}
