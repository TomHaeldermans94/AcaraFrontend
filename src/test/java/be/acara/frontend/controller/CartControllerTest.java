package be.acara.frontend.controller;

import be.acara.frontend.domain.Cart;
import be.acara.frontend.model.CartModel;
import be.acara.frontend.model.CreateOrderModel;
import be.acara.frontend.model.EventModel;
import be.acara.frontend.security.TokenLogoutHandler;
import be.acara.frontend.service.CartService;
import be.acara.frontend.service.SecurityService;
import be.acara.frontend.service.UserService;
import be.acara.frontend.service.mapper.CartMapper;
import be.acara.frontend.util.CartUtil;
import be.acara.frontend.util.EventUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
public class CartControllerTest {
    @MockBean
    private AuthenticationProvider authenticationProvider;
    @MockBean
    private TokenLogoutHandler tokenLogoutHandler;
    @MockBean
    private UserService userService;
    
    @MockBean
    private PasswordEncoder passwordEncoder;
    
    @MockBean(name = "securityService") // name is required or it WILL break the method security methods!
    private SecurityService securityService;
    
    @MockBean
    private CartService cartService;
    @MockBean
    private CartMapper cartMapper;
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    @WithMockUser
    void getCurrentCart() throws Exception {
        Cart cart = CartUtil.cart();
        CartModel cartModel = CartUtil.cartModel();
        when(cartService.getCart()).thenReturn(cart);
        when(cartMapper.cartToCartModel(cart)).thenReturn(cartModel);
        
        mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart/cart"))
                .andExpect(model().attribute("cartModel", cartModel));
    }
    
    @Test
    @WithMockUser
    void addEvent() throws Exception {
        Cart cart = CartUtil.cart();
        CartModel cartModel = CartUtil.cartModel();
        EventModel eventModel = EventUtil.firstEvent();
        CreateOrderModel createOrderModel = new CreateOrderModel(eventModel, 1);
        when(cartService.addToCart(createOrderModel)).thenReturn(cart);
        when(cartMapper.cartToCartModel(cart)).thenReturn(cartModel);
        
        mockMvc.perform(post("/cart")
                .flashAttr("createOrderModel", createOrderModel))
                .andExpect(status().isOk())
                .andExpect(model().attribute("cartModel", cartModel));
    }
}
