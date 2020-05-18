package be.acara.frontend.service;

import be.acara.frontend.domain.Cart;
import be.acara.frontend.domain.CartItem;
import be.acara.frontend.domain.User;
import be.acara.frontend.model.CreateOrderModel;
import be.acara.frontend.repository.CartRepository;
import be.acara.frontend.util.EventUtil;
import be.acara.frontend.util.UserUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {
    @Mock
    private CartRepository cartRepository;
    @Mock
    private UserService userService;
    @Mock
    private EventService eventService;
    @InjectMocks
    private CartServiceImpl cartService;
    
    @Test
    void getCart() {
        User user = UserUtil.firstUserDomain();
        when(userService.getCurrentUser()).thenReturn(user);
        when(cartRepository.findByUserUsername(user.getUsername())).thenReturn(Optional.of(new Cart(user)));
    
        Cart answer = cartService.getCart();
        
        assertThat(answer).isNotNull();
        
        verify(cartRepository, times(1)).findByUserUsername(user.getUsername());
        verify(userService, times(1)).getCurrentUser();
    }
    
    @Test
    void getCart_notFound() {
        User user = UserUtil.firstUserDomain();
        when(userService.getCurrentUser()).thenReturn(user);
        when(cartRepository.findByUserUsername(user.getUsername())).thenReturn(Optional.empty());
    
        Cart answer = cartService.getCart();
    
        assertThat(answer).isNotNull();
    
        verify(cartRepository, times(1)).findByUserUsername(user.getUsername());
        verify(userService, times(1)).getCurrentUser();
    }
    
    @Test
    void addToCart() {
        User user = UserUtil.firstUserDomain();
        when(userService.getCurrentUser()).thenReturn(user);
        when(cartRepository.findByUserUsername(user.getUsername())).thenReturn(Optional.of(new Cart(user)));
        when(eventService.getEvent(EventUtil.firstEvent().getId())).thenReturn(EventUtil.firstEventDto());
    
        CreateOrderModel createOrderModel = new CreateOrderModel(EventUtil.firstEvent(), 1);
    
        Cart answer = cartService.addToCart(createOrderModel);
        
        assertThat(answer.getItems()).hasSize(1);
        assertThat(answer.getItems()).extracting(CartItem::getAmount).containsExactly(createOrderModel.getAmountOfTickets());
        assertThat(answer.getItems()).extracting(CartItem::getEventId).containsExactly(createOrderModel.getEventModel().getId());
    }
    
    @Test
    void clearCart() {
        User user = UserUtil.firstUserDomain();
        when(userService.getCurrentUser()).thenReturn(user);
        when(cartRepository.findByUserUsername(user.getUsername())).thenReturn(Optional.of(new Cart(user)));
        
        cartService.clearCart();
        
        verify(cartRepository, times(1)).saveAndFlush(any(Cart.class));
    }
}
