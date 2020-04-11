package be.acara.frontend.controller;

import be.acara.frontend.domain.User;
import be.acara.frontend.model.Event;
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
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
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
        List<Event> events = eventMapper.map(eventFeignClient.getAllEventsFromSelectedUser(id)).getEventList();
        model.addAttribute("user", user);
        model.addAttribute("events", events);
        return "user/userDetails";
    }

    @GetMapping("/{id}")
    public String displayEditUserForm(@PathVariable("id") Long id, Model model) {
        User user = userMapper.map(userFeignClient.getUserById(id));
        model.addAttribute("editUser", user);
        return "user/editUser";
    }

    @PostMapping("/{id}")
    public String handleEditEventForm(@Valid @ModelAttribute("editUser") User user, BindingResult br) {
        User userFromDb = userMapper.map(userFeignClient.getUserById(user.getId()));
        if (br.hasErrors()) {
            for (ObjectError error : br.getAllErrors()) {
                String field = ((FieldError) error).getField();
                System.out.println(field);
            }
            return "user/editUser";
        }
        userFeignClient.editUser(userFromDb.getId(), userMapper.map(user));
        return "redirect:/events";
    }
}
