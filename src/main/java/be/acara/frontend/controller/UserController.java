package be.acara.frontend.controller;

import be.acara.frontend.domain.User;
import be.acara.frontend.model.EventModelList;
import be.acara.frontend.model.UserModel;
import be.acara.frontend.service.EventService;
import be.acara.frontend.service.SecurityService;
import be.acara.frontend.service.UserService;
import be.acara.frontend.service.mapper.EventMapper;
import be.acara.frontend.service.mapper.UserMapper;
import be.acara.frontend.util.PaginationUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/users")
public class UserController {
    
    private final UserService userService;
    private final SecurityService securityService;
    private final UserMapper userMapper;
    private final EventService eventService;
    private final EventMapper eventMapper;
    
    private static final String EDIT_USER_LOCATION = "user/editUser";
    private static final String USER_REGISTRATION_LOCATION = "user/registration";
    private static final String USER_DETAILS_LOCATION = "user/userDetails";
    private static final String REDIRECT_EVENTS = "redirect:/events";
    private static final String FORWARD_USER_DETAILS = "forward:/users/detail";
    private static final String ATTRIBUTE_USER_FORM = "userForm";
    private static final String ATTRIBUTE_USER = "user";
    
    public UserController(UserService userService,
                          SecurityService securityService,
                          UserMapper userMapper,
                          EventService eventService,
                          EventMapper eventMapper) {
        this.userService = userService;
        this.securityService = securityService;
        this.userMapper = userMapper;
        this.eventService = eventService;
        this.eventMapper = eventMapper;
    }
    
    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute(ATTRIBUTE_USER_FORM, new UserModel());
        return USER_REGISTRATION_LOCATION;
    }
    
    @PostMapping("/registration")
    public String registration(@Valid @ModelAttribute(ATTRIBUTE_USER_FORM) UserModel userForm, BindingResult br) {
        if (br.hasErrors()) {
            return USER_REGISTRATION_LOCATION;
        }
        User user = userMapper.userModelToUser(userForm);
        userService.save(user);
        securityService.autoLogin(user.getUsername(), user.getPasswordConfirm());
        return REDIRECT_EVENTS;
    }
    
    @GetMapping("/detail/{id}")
    @PreAuthorize("@securityService.hasUserId(authentication, #id) or hasRole('admin')")
    public String displayUser(@PathVariable("id") Long id, Model model,
                              @RequestParam(name = "pageSubscribedEvents", defaultValue = "1", required = false) int pageSubscribedEvents,
                              @RequestParam(name = "sizeSubscribedEvents", defaultValue = "3", required = false) int sizeSubscribedEvents,
                              @RequestParam(name = "pageLikedEvents", defaultValue = "1", required = false) int pageLikedEvents,
                              @RequestParam(name = "sizeLikedEvents", defaultValue = "3", required = false) int sizeLikedEvents) {
        User user = userService.getUser(id);
        
        EventModelList subscribedEvents = eventMapper.eventDtoListToEventModelList(eventService.getEventsFromUser(id, pageSubscribedEvents - 1, sizeSubscribedEvents));
        PaginationUtil.addPageNumbers(subscribedEvents, model, "pageNumbersSubscribedEvents");
        EventModelList likedEvents = eventMapper.eventDtoListToEventModelList(eventService.getEventsThatUserLiked(id, pageLikedEvents - 1, sizeLikedEvents));
        PaginationUtil.addPageNumbers(likedEvents, model, "pageNumbersLikedEvents");
        
        model.addAttribute(ATTRIBUTE_USER, user);
        model.addAttribute("subscribedEvents", subscribedEvents);
        model.addAttribute("likedEvents", likedEvents);
        addLocalDateTime(model);
    
        return USER_DETAILS_LOCATION;
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("@securityService.hasUserId(authentication, #id) or hasRole('admin')")
    public String displayEditUserForm(@PathVariable("id") Long id, Model model) {
        UserModel user = userMapper.userToUserModel(userService.getUser(id));
        model.addAttribute(ATTRIBUTE_USER_FORM, user);
        return EDIT_USER_LOCATION;
    }
    
    @PostMapping("/{id}")
    @PreAuthorize("@securityService.hasUserId(authentication, #user.id) or hasRole('admin')")
    public String handleEditUserForm(@Valid @ModelAttribute(ATTRIBUTE_USER_FORM) UserModel user, BindingResult br) {
        if (br.hasErrors()) {
            return EDIT_USER_LOCATION;
        }
        userService.editUser(user);
        return REDIRECT_EVENTS;
    }
    
    @PostMapping("/{location}/likes/{eventId}/{relatedEventId}")
    public String likeOrDislikeEvent(@RequestParam("liked") boolean liked,
                                     @PathVariable("location") String location,
                                     @PathVariable("eventId") Long eventId,
                                     @PathVariable("relatedEventId") Long relatedEventId) {
        if (liked && relatedEventId == 0) {
            userService.dislikeEvent(eventId);
        } else if (!liked && relatedEventId == 0) {
            userService.likeEvent(eventId);
        } else if (liked) {
            userService.dislikeEvent(relatedEventId);
        } else {
            userService.likeEvent(relatedEventId);
        }
        String targetUrl = REDIRECT_EVENTS;
        if ("details".equals(location)) {
            targetUrl = "redirect:/events/detail/" + eventId;
        }
        return targetUrl;
    }
    
    @GetMapping("/profile")
    public String getProfile(Principal principal) {
        return String.format("%s/%d", FORWARD_USER_DETAILS, userService.findByUsername(principal.getName()).getId());
    }

    private void addLocalDateTime(Model model) {
        model.addAttribute("now", LocalDateTime.now());
    }
}
