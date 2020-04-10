package be.acara.frontend.controller;

import be.acara.frontend.controller.dto.EventDto;
import be.acara.frontend.domain.User;
import be.acara.frontend.service.EventFeignClient;
import be.acara.frontend.service.SecurityService;
import be.acara.frontend.service.UserFeignClient;
import be.acara.frontend.service.UserService;
import be.acara.frontend.service.mapper.EventMapper;
import be.acara.frontend.service.mapper.UserMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    private final EventMapper eventMapper;
    
    public UserController(UserService userService, SecurityService securityService, UserFeignClient userFeignClient, EventFeignClient eventFeignClient, UserMapper userMapper, EventMapper eventMapper) {
        this.userService = userService;
        this.securityService = securityService;
        this.userFeignClient = userFeignClient;
        this.eventFeignClient = eventFeignClient;
        this.userMapper = userMapper;
        this.eventMapper = eventMapper;
    }
    
    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userForm", new User());
        return "user/registration";
    }
    
    @PostMapping("/registration")
    public String registration(@Valid @ModelAttribute("userForm") User userForm, BindingResult br) {
        if (br.hasErrors()) {
            return "user/registration";
        }
        
        userService.save(userForm);
        securityService.autoLogin(userForm.getUsername(), userForm.getPasswordConfirm());
        
        return "redirect:/events";
    }
    
    @GetMapping("/detail/{id}")
    public String displayEvent(@PathVariable("id") Long id, ModelMap model) {
        User user = userMapper.map(userFeignClient.getUserById(id));
        List<EventDto> events = eventFeignClient.getAllEventsFromSelectedUser(id).getContent();
        model.addAttribute("user", user);
        model.addAttribute("events", events);
        return "userDetails";
    }
}
