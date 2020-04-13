package be.acara.frontend.controller;

import be.acara.frontend.controller.dto.EventDtoList;
import be.acara.frontend.controller.dto.UserDto;
import be.acara.frontend.domain.User;
import be.acara.frontend.model.EventList;
import be.acara.frontend.service.EventFeignClient;
import be.acara.frontend.service.UserFeignClient;
import be.acara.frontend.service.UserService;
import be.acara.frontend.service.mapper.EventMapper;
import be.acara.frontend.service.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static be.acara.frontend.util.EventUtil.createEventDtoList;
import static be.acara.frontend.util.EventUtil.createEventList;
import static be.acara.frontend.util.UserUtil.firstUser;
import static be.acara.frontend.util.UserUtil.firstUserDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserFeignClient userFeignClient;
    @Mock
    private EventFeignClient eventFeignClient;
    @Mock
    private UserMapper userMapper;
    @Mock
    private EventMapper eventMapper;
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void findById() throws Exception {
        Long id = 1L;
        UserDto userDto = firstUserDto();
        User user = firstUser();
        EventDtoList eventDtoList = createEventDtoList();
        EventList eventList = createEventList();
        when(userFeignClient.getUserById(id)).thenReturn(userDto);
        when(eventFeignClient.getAllEventsFromSelectedUser(id)).thenReturn(eventDtoList);
        when(userMapper.map(userDto)).thenReturn(user);
        when(eventMapper.map(eventDtoList)).thenReturn(eventList);

        mockMvc.perform(get("/users/detail/{id}",id))
                .andExpect(status().isOk())
                .andExpect(view().name("user/userDetails"))
                .andExpect(model().attribute("events", eventMapper.map(eventDtoList).getEventList()))
                .andExpect(model().attribute("user", userMapper.map(userDto)));
    }

    @Test
    void displayEditUserForm() throws Exception {
        Long id = 1L;
        when(userFeignClient.getUserById(id)).thenReturn(firstUserDto());
        when(userMapper.map(firstUserDto())).thenReturn(firstUser());

        mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("user/editUser"))
                .andExpect(model().attribute("editUser", firstUser()));
    }

    @Test
    void handleEditUserForm() throws Exception{
        Long id = 1L;
        doNothing().when(userService).editUser(any());

        mockMvc.perform(post("/users/{id}", id)
                .flashAttr("editUser", firstUser()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/events"))
                .andExpect(model().hasNoErrors());
    }

    @Test
    void showNewModelAndViewInCaseOfErrorsEditUser() throws Exception {
        Long id = 1L;
        User user = firstUser();
        user.setFirstName("");

        mockMvc.perform(post("/users/{id}", id)
                .flashAttr("editUser", user))
                .andExpect(status().isOk())
                .andExpect(view().name("user/editUser"))
                .andExpect(model().attributeHasFieldErrors("editUser","firstName"));
    }
}
