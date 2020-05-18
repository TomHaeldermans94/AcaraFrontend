package be.acara.frontend.controller;

import be.acara.frontend.controller.dto.EventDtoList;
import be.acara.frontend.domain.User;
import be.acara.frontend.model.EventModelList;
import be.acara.frontend.model.UserModel;
import be.acara.frontend.security.MethodSecurityConfigurer;
import be.acara.frontend.security.TokenLogoutHandler;
import be.acara.frontend.service.EventService;
import be.acara.frontend.service.SecurityService;
import be.acara.frontend.service.UserService;
import be.acara.frontend.service.mapper.EventMapper;
import be.acara.frontend.service.mapper.UserMapper;
import be.acara.frontend.util.UserUtil;
import be.acara.frontend.util.WithMockAdmin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.security.Principal;
import java.util.Collections;

import static be.acara.frontend.util.EventUtil.createEventDtoList;
import static be.acara.frontend.util.EventUtil.createEventModelList;
import static be.acara.frontend.util.UserUtil.firstUser;
import static be.acara.frontend.util.UserUtil.firstUserDomain;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(MethodSecurityConfigurer.class)
public class UserControllerTest {
    @MockBean
    private AuthenticationProvider authenticationProvider;
    @MockBean
    private TokenLogoutHandler tokenLogoutHandler;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean(name = "securityService") // name is required or it WILL break the method security methods!
    private SecurityService securityService;
    @MockBean
    private UserService userService;
    @MockBean
    private UserMapper userMapper;
    @MockBean
    private EventService eventService;
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
        EventModelList eventModelList = createEventModelList();
        when(userService.getUser(id)).thenReturn(user);
        when(eventMapper.eventDtoListToEventModelList(any())).thenReturn(eventModelList);
        when(eventService.getEventsFromUser(anyLong(), anyInt(), anyInt())).thenReturn(eventDtoList);
        when(eventService.getEventsThatUserLiked(anyLong(), anyInt(), anyInt())).thenReturn(eventDtoList);
        when(securityService.hasUserId(any(), anyLong())).thenReturn(true);

        mockMvc.perform(get("/users/detail/{id}",id))
                .andExpect(status().isOk())
                .andExpect(view().name("user/userDetails"))
                .andExpect(model().attribute("subscribedEvents", eventModelList))
                .andExpect(model().attribute("likedEvents", eventModelList))
                .andExpect(model().attribute("user", user));
    }
    
    @Test
    @WithMockAdmin
    void displayUser_withEmptyPage() throws Exception {
        Long id = 1L;
        User user = firstUserDomain();
        EventDtoList eventDtoList = new EventDtoList(Collections.emptyList());
        EventModelList eventModelList = new EventModelList(Collections.emptyList(), Pageable.unpaged(), 0);
        when(userService.getUser(id)).thenReturn(user);
        when(eventMapper.eventDtoListToEventModelList(any())).thenReturn(eventModelList);
        when(eventService.getEventsFromUser(anyLong(), anyInt(), anyInt())).thenReturn(eventDtoList);
        when(eventService.getEventsThatUserLiked(anyLong(), anyInt(), anyInt())).thenReturn(eventDtoList);
        when(securityService.hasUserId(any(), anyLong())).thenReturn(true);
        
        mockMvc.perform(get("/users/detail/{id}",id))
                .andExpect(status().isOk())
                .andExpect(view().name("user/userDetails"))
                .andExpect(model().attribute("subscribedEvents", eventModelList))
                .andExpect(model().attribute("likedEvents", eventModelList))
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
    @WithMockUser(roles = {"USER","ADMIN"})
    void shouldNotBeAbleToDisplayUser_asNormalUser_withIdOfOtherUser() throws Exception {
        Long id = Long.MAX_VALUE;

        MvcResult mvcResult = mockMvc.perform(get("/users/detail/{id}", id))
                .andExpect(status().isOk())
                .andExpect(MvcResult::getResolvedException)
                .andReturn();
    
        Exception resolvedException = mvcResult.getResolvedException();
        assertThat(resolvedException).isInstanceOf(AccessDeniedException.class);


        verify(securityService, times(1)).hasUserId(any(), anyLong());
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
        when(securityService.hasUserId(any(), anyLong())).thenReturn(true);

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
        when(securityService.hasUserId(any(), anyLong())).thenReturn(true);

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

        UserModel userModel = firstUser();
        when(securityService.hasUserId(any(), anyLong())).thenReturn(true);

        mockMvc.perform(post("/users/{id}", id)
                .flashAttr("userForm", userModel))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/events"));
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
        when(securityService.hasUserId(any(), anyLong())).thenReturn(true);

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
    
    @Test
    @WithMockUser
    void getUserProfile() throws Exception {
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(firstUserDomain().getUsername());
        when(userService.findByUsername(any())).thenReturn(UserUtil.firstUserDomain());
        
        mockMvc.perform(get("/users/profile"))
                .andExpect(forwardedUrl(String.format("/users/detail/%d", firstUserDomain().getId())));
    }
}
