package be.acara.frontend.controller;

import be.acara.frontend.controller.dto.EventDto;
import be.acara.frontend.domain.User;
import be.acara.frontend.model.UserModel;
import be.acara.frontend.service.EventService;
import be.acara.frontend.service.SecurityService;
import be.acara.frontend.service.UserService;
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
    private final UserMapper userMapper;
    private final EventService eventService;
    
    private static final String ATTRIBUTE_USER_FORM = "userForm";
    private static final String ATTRIBUTE_EDIT_USER_REDIRECT = "user/editUser";
    private static final String ATTRIBUTE_USER = "user";
    private static final String ATTRIBUTE_EVENTS = "events";
    
    public UserController(UserService userService, SecurityService securityService, UserMapper userMapper, EventService eventService) {
        this.userService = userService;
        this.securityService = securityService;
        this.userMapper = userMapper;
        this.eventService = eventService;
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
    public String displayUser(@PathVariable("id") Long id, ModelMap model) {
        User user = userService.getUser(id);
        List<EventDto> events = eventService.getEventsFromUser(id).getContent();
        model.addAttribute(ATTRIBUTE_USER, user);
        model.addAttribute(ATTRIBUTE_EVENTS, events);
        return "user/userDetails";
    }
    
    @GetMapping("/{id}")
    public String displayEditUserForm(@PathVariable("id") Long id, Model model) {
        UserModel user = userMapper.userToUserModel(userService.getUser(id));
        model.addAttribute("editUser", user);
        return ATTRIBUTE_EDIT_USER_REDIRECT;
    }
    
    @PostMapping("/{id}")
    public String handleEditEventForm(@ModelAttribute("editUser") @Valid UserModel user, BindingResult br) {
        if (br.hasErrors()) {
            return ATTRIBUTE_EDIT_USER_REDIRECT;
        }
        if (user.getPassword().equals(user.getPasswordConfirm())){
            userService.editUser(user);
            return "redirect:/events";
        }
        return ATTRIBUTE_EDIT_USER_REDIRECT;
    }
}
