package be.acara.frontend.controller;

import be.acara.frontend.controller.dto.EventDtoList;
import be.acara.frontend.domain.User;
import be.acara.frontend.model.UserModel;
import be.acara.frontend.security.TokenLogoutHandler;
import be.acara.frontend.service.*;
import be.acara.frontend.service.mapper.EventMapper;
import be.acara.frontend.service.mapper.UserMapper;
import be.acara.frontend.util.WithMockAdmin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static be.acara.frontend.util.EventUtil.createEventDtoList;
import static be.acara.frontend.util.UserUtil.firstUser;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @MockBean
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDetailsService;
    @MockBean
    private AuthenticationProvider authenticationProvider;
    @MockBean
    private TokenLogoutHandler tokenLogoutHandler;
    
    @MockBean
    private UserService userService;
    @MockBean
    private SecurityService securityService;
    @MockBean
    private UserMapper userMapper;
    @MockBean
    private EventService eventService;
    
    @MockBean
    private UserFeignClient userFeignClient;
    @MockBean
    private EventFeignClient eventFeignClient;
    @MockBean
    private EventMapper eventMapper;

    @Autowired
    private MockMvc mockMvc;
    
    @AfterEach
    void tearDown() {
        reset(userService);
    }
    
    @Test
    @WithMockAdmin
    void displayUser() throws Exception {
        Long id = 1L;
        User user = firstUser();
        EventDtoList eventDtoList = createEventDtoList();
        when(userService.getUser(id)).thenReturn(user);
        when(eventService.getEventsFromUser(anyLong())).thenReturn(eventDtoList);

        mockMvc.perform(get("/users/detail/{id}",id))
                .andExpect(status().isOk())
                .andExpect(view().name("userDetails"))
                .andExpect(model().attribute("events", eventDtoList.getContent()))
                .andExpect(model().attribute("user", user));
    }
    
    @Test
    @WithAnonymousUser
    void shouldNotBeAbleToDisplayUser_asAnonymousUser() throws Exception {
        Long id = 1L;
        
        mockMvc.perform(get("/users/detail/{id}", id))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("http://localhost/login"));
    }
    
    @Test
    @WithMockUser
    void shouldNotBeAbleToDisplayUser_asNormalUser() throws Exception {
        Long id = 1L;
    
        mockMvc.perform(get("/users/detail/{id}", id))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithAnonymousUser
    void shouldBeAbleToDisplayRegistration_asAnonymousUser() throws Exception {
        mockMvc.perform(get("/users/registration"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("userForm", new UserModel()))
                .andExpect(view().name("user/registration"))
                .andExpect(content().string(containsString("<title>Registration</title>")));
    }
    
    @Test
    @WithMockUser
    void shouldNotBeAbleToDisplayRegistration_asUser() throws Exception {
        mockMvc.perform(get("/users/registration"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockAdmin
    void shouldNotBeAbleToDisplayRegistration_asAdmin() throws Exception {
        mockMvc.perform(get("/users/registration"))
                .andExpect(status().isForbidden());
    }
}
