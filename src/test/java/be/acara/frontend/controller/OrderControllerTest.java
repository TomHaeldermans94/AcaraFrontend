package be.acara.frontend.controller;

import be.acara.frontend.security.TokenLogoutHandler;
import be.acara.frontend.service.EventService;
import be.acara.frontend.service.OrderService;
import be.acara.frontend.service.UserService;
import be.acara.frontend.service.mapper.EventMapper;
import be.acara.frontend.util.EventUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @MockBean
    private OrderService orderService;

    @MockBean
    private EventMapper mapper;

    @MockBean
    private EventService eventService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userDetailsService;
    @MockBean
    private AuthenticationProvider authenticationProvider;
    @MockBean
    private TokenLogoutHandler tokenLogoutHandler;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser
    void displayOrderForm_asUser() throws Exception {
        Long eventId = 1L;
        when(mapper.eventDtoToEventModel(any())).thenReturn(EventUtil.firstEvent());
        mockMvc.perform(get("/orders")
                .param("event", String.valueOf(eventId)))
                .andExpect(status().isOk())
                .andExpect(view().name("buyOrder"));
    }

    @Test
    @WithAnonymousUser
    void displayOrderForm_asAnonymousUser() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser
    void handlecreateOrderForm() throws Exception {
        Long eventId = 1L;
        doNothing().when(orderService).create(eventId);

        mockMvc.perform(post("/orders")
                .param("event", String.valueOf(eventId))
        )
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/events"));
    }
}
