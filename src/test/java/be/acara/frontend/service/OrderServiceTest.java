package be.acara.frontend.service;

import be.acara.frontend.controller.dto.CreateOrderDto;
import be.acara.frontend.controller.dto.CreateOrderList;
import be.acara.frontend.domain.Cart;
import be.acara.frontend.domain.CartItem;
import be.acara.frontend.model.CreateOrderModel;
import be.acara.frontend.service.mapper.OrderMapper;
import be.acara.frontend.util.EventUtil;
import be.acara.frontend.util.UserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    private OrderService orderService;
    @Mock
    private OrderFeignClient orderFeignClient;
    private OrderMapper orderMapper;
    
    @BeforeEach
    void setUp() {
        orderMapper = OrderMapper.INSTANCE;
        orderService = new OrderServiceImpl(orderMapper, orderFeignClient);
    }
    
    @Test
    void create() {
        CreateOrderModel createOrderModel = new CreateOrderModel(EventUtil.firstEvent(), 1);
        
        orderService.create(createOrderModel);
        
        verify(orderFeignClient, times(1)).create(orderMapper.createOrderModelToCreateOrderDto(createOrderModel));
    }
    
    @Test
    void createBatch() {
        Cart cart = new Cart(
                1L,
                UserUtil.firstUserDomain(),
                Set.of(new CartItem(
                        1L,
                        null,
                        1L,
                        1,
                        BigDecimal.ONE
                )),
                BigDecimal.ONE
        );
        
        orderService.create(cart);
        
        verify(orderFeignClient, times(1)).create(any(CreateOrderList.class));
    }
    
    @Test
    void edit() {
        Long id = 1L;
        CreateOrderDto createOrderDto = new CreateOrderDto(id, 1);
        
        orderService.edit(id, createOrderDto);
        
        verify(orderFeignClient, times(1)).edit(id, createOrderDto);
    }
    
    @Test
    void remove() {
        Long id = 1L;
        
        orderService.remove(id);
        
        verify(orderFeignClient, times(1)).remove(id);
    }
}
