package be.acara.frontend.controller;

import be.acara.frontend.controller.dto.EventDto;
import be.acara.frontend.domain.User;
import be.acara.frontend.model.UserModel;
import be.acara.frontend.service.EventFeignClient;
import be.acara.frontend.service.SecurityService;
import be.acara.frontend.service.UserFeignClient;
import be.acara.frontend.service.UserService;
import be.acara.frontend.service.mapper.UserMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {
    
    private final UserService userService;
    private final SecurityService securityService;
    private final UserFeignClient userFeignClient;
    private final EventFeignClient eventFeignClient;
    private final UserMapper userMapper;
    
    private static final String ATTRIBUTE_USER_FORM = "userForm";
    private static final String ATTRIBUTE_USER = "user";
    private static final String ATTRIBUTE_EVENTS = "events";
    
    public UserController(UserService userService, SecurityService securityService, UserFeignClient userFeignClient, EventFeignClient eventFeignClient, UserMapper userMapper) {
        this.userService = userService;
        this.securityService = securityService;
        this.userFeignClient = userFeignClient;
        this.eventFeignClient = eventFeignClient;
        this.userMapper = userMapper;
    }
    
    @GetMapping("/registration")
    public String registration(ModelMap model) {
        model.addAttribute(ATTRIBUTE_USER_FORM, new UserModel());
        return "user/registration";
    }
    
    @PostMapping("/registration")
    public String registration(@Valid @ModelAttribute(ATTRIBUTE_USER_FORM) UserModel userForm, BindingResult br) {
        if (br.hasErrors()) {
            return "user/registration";
        }
        
        User user = userMapper.userModelToUser(userForm);
        userService.save(user);
        securityService.autoLogin(user.getUsername(), user.getPasswordConfirm());
        return "redirect:/events";
    }
    
    @GetMapping("/detail/{id}")
    public String displayEvent(@PathVariable("id") Long id, ModelMap model) {
        User user = userMapper.userDtoToUser(userFeignClient.getUserById(id));
        List<EventDto> events = eventFeignClient.getAllEventsFromSelectedUser(id).getContent();
        model.addAttribute(ATTRIBUTE_USER, user);
        model.addAttribute(ATTRIBUTE_EVENTS, events);
        return "userDetails";
    }
}
