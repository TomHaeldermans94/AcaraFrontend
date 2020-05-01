package be.acara.frontend.controller;

import be.acara.frontend.controller.dto.EventDtoList;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/users")
public class UserController {
    
    private final UserService userService;
    private final SecurityService securityService;
    private final UserMapper userMapper;
    private final EventService eventService;
    
    private static final String ATTRIBUTE_USER_FORM = "userForm";
    private static final String REDIRECT_EVENTS = "redirect:/events";
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
    public String displayUser(@PathVariable("id") Long id, ModelMap model,
                              @RequestParam(name = "page", defaultValue = "1", required = false) int page,
                              @RequestParam(name = "size", defaultValue = "20", required = false) int size) {
        User user = userService.getUser(id);
        EventDtoList subscribedEvents = eventService.getEventsFromUser(id, page-1, size);
        List<Integer> pageNumbersSubscribedEvent = getPageNumbers(subscribedEvents);
        if(pageNumbersSubscribedEvent != null) {
            model.addAttribute("pageNumbersSubscribedEvents", pageNumbersSubscribedEvent);
        }
        EventDtoList likedEvents = eventService.getEventsThatUserLiked(id, page-1, size);
        List<Integer> pageNumbersLikedEvents = getPageNumbers(likedEvents);
        if(pageNumbersLikedEvents != null) {
            model.addAttribute("pageNumbersLikedEvents", pageNumbersLikedEvents);

        }
        model.addAttribute(ATTRIBUTE_USER, user);
        model.addAttribute("subscribedEvents", subscribedEvents);
        model.addAttribute("likedEvents", likedEvents);
        return "user/userDetails";
    }

    private List<Integer> getPageNumbers(EventDtoList events) {
        int totalPagesLikedEvents = events.getTotalPages();
        List<Integer> pageNumbers = null;
        if (totalPagesLikedEvents > 0) {
            pageNumbers = IntStream.rangeClosed(1, totalPagesLikedEvents)
                    .boxed()
                    .collect(Collectors.toList());
        }
        return pageNumbers;
    }
    
    @GetMapping("/{id}")
    public String displayEditUserForm(@PathVariable("id") Long id, Model model) {
        UserModel user = userMapper.userToUserModel(userService.getUser(id));
        model.addAttribute(ATTRIBUTE_USER_FORM, user);
        return ATTRIBUTE_EDIT_USER_REDIRECT;
    }
    
    @PostMapping("/{id}")
    public String handleEditUserForm(@Valid @ModelAttribute(ATTRIBUTE_USER_FORM) UserModel user, BindingResult br) {
        if (br.hasErrors()) {
            return ATTRIBUTE_EDIT_USER_REDIRECT;
        }
        userService.editUser(user);
        return "redirect:/events";
    }

    @GetMapping("/{id}/{location}/likes")
    public String likeEvent(@PathVariable("id") Long id, @PathVariable("location") String location) {
        userService.like(id);
        String targetUrl = REDIRECT_EVENTS;
        if("details".equals(location)) {
            targetUrl = "redirect:/events/detail/" + id;
        }
        return targetUrl;
    }
}
