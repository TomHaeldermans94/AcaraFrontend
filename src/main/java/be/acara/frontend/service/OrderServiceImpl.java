package be.acara.frontend.service;

import be.acara.frontend.controller.dto.CreateOrderDto;
import be.acara.frontend.model.CreateOrderModel;
import be.acara.frontend.service.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderFeignClient orderFeignClient;

    @Autowired
    public OrderServiceImpl(OrderMapper orderMapper, OrderFeignClient orderFeignClient) {
        this.orderMapper = orderMapper;
        this.orderFeignClient = orderFeignClient;
    }


    @Override
    public void create(CreateOrderModel createOrderModel) {
        orderFeignClient.create(
                orderMapper.createOrderModelToCreateOrderDto(createOrderModel)
        );
    }

    @Override
    public void edit(Long id, CreateOrderDto createOrderDto) {
        orderFeignClient.edit(id, createOrderDto);
    }

    @Override
    public void remove(Long id) {
        orderFeignClient.remove(id);
    }

}
