package be.acara.frontend.controller;

import be.acara.frontend.controller.dto.EventDtoList;
import be.acara.frontend.domain.User;
import be.acara.frontend.model.UserModel;
import be.acara.frontend.security.TokenLogoutHandler;
import be.acara.frontend.service.EventService;
import be.acara.frontend.service.SecurityService;
import be.acara.frontend.service.UserFeignClient;
import be.acara.frontend.service.UserService;
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
import static be.acara.frontend.util.UserUtil.firstUserDomain;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        User user = firstUserDomain();
        EventDtoList eventDtoList = createEventDtoList();
        when(userService.getUser(id)).thenReturn(user);
        when(eventService.getEventsFromUser(anyLong(), anyInt(), anyInt())).thenReturn(eventDtoList);
        when(eventService.getEventsThatUserLiked(anyLong(), anyInt(), anyInt())).thenReturn(eventDtoList);

        mockMvc.perform(get("/users/detail/{id}",id))
                .andExpect(status().isOk())
                .andExpect(view().name("user/userDetails"))
                .andExpect(model().attribute("subscribedEvents", eventDtoList))
                .andExpect(model().attribute("likedEvents", eventDtoList))
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
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/forbidden"));
    }
    
    @Test
    @WithAnonymousUser
    void shouldBeAbleToDisplayRegistration_asAnonymousUser() throws Exception {
        mockMvc.perform(get("/users/registration"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("userForm", new UserModel()))
                .andExpect(view().name("user/registration"))
                .andExpect(content().string(containsString("<title>Add User</title>")));
    }
    
    @Test
    @WithMockUser
    void shouldNotBeAbleToDisplayRegistration_asUser() throws Exception {
        mockMvc.perform(get("/users/registration"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/forbidden"));
    }
    
    @Test
    @WithMockAdmin
    void shouldNotBeAbleToDisplayRegistration_asAdmin() throws Exception {
        mockMvc.perform(get("/users/registration"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/forbidden"));
    }

    @Test
    @WithMockAdmin
    void displayEditUserForm() throws Exception {
        Long id = 1L;
        User user = firstUserDomain();
        UserModel userModel = firstUser();
        when(userService.getUser(id)).thenReturn(user);
        when(userMapper.userToUserModel(user)).thenReturn(userModel);

        mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("user/editUser"))
                .andExpect(model().attribute("userForm", userModel));
    }

    @Test
    @WithMockAdmin
    void handleEditUserForm() throws Exception{
        Long id = 1L;
        doNothing().when(userService).editUser(any());

        mockMvc.perform(post("/users/{id}", id)
                .flashAttr("userForm", firstUser()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/events"))
                .andExpect(model().hasNoErrors());
    }
    
    @Test
    @WithMockUser
    void handleEditUserForm_asUser() throws Exception {
        Long id = 1L;
        
        mockMvc.perform(post("/users/{id}", id))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/forbidden"));
    }
    
    @Test
    @WithAnonymousUser
    void handleEditUserForm_asAnonymous() throws Exception {
        Long id = 1L;
        
        mockMvc.perform(post("/users/{id}", id))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockAdmin
    void showNewModelAndViewInCaseOfErrorsEditUser() throws Exception {
        Long id = 1L;
        User user = firstUserDomain();
        user.setFirstName("");
        UserModel userModel = UserMapper.INSTANCE.userToUserModel(user);
        userModel.setPassword("pw");
        userModel.setPasswordConfirm("pw");
        
        mockMvc.perform(post("/users/{id}", id)
                .flashAttr("editUser", userModel))
                .andExpect(status().isOk())
                .andExpect(view().name("user/editUser"))
                .andExpect(model().attributeHasFieldErrors("userForm","firstName"));
    }
    
    @Test
    @WithAnonymousUser
    void register() throws Exception {
        UserModel userModel = firstUser();
        userModel.setId(null);
        User user = UserMapper.INSTANCE.userModelToUser(userModel);
        
        when(userMapper.userModelToUser(userModel)).thenReturn(user);
        doNothing().when(userService).save(user);
        doNothing().when(securityService).autoLogin(anyString(), anyString());
        
        mockMvc.perform(post("/users/registration")
                .flashAttr("userForm", userModel))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/events"));
    }
    
    @Test
    @WithAnonymousUser
    void registerWithErrors() throws Exception {
        UserModel userModel = firstUser();
        userModel.setFirstName(null);
    
        mockMvc.perform(post("/users/registration")
                .flashAttr("userForm", userModel))
                .andExpect(status().isOk())
                .andExpect(view().name("user/registration"));
    }
    
    @Test
    @WithAnonymousUser
    void registerWithUnequalPasswords() throws Exception {
        UserModel userModel = firstUser();
        userModel.setPasswordConfirm("not the password");
        
        mockMvc.perform(post("/users/registration")
                .flashAttr("userForm", userModel))
                .andExpect(status().isOk())
                .andExpect(view().name("user/registration"));
    }
}
