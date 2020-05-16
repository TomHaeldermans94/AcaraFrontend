package be.acara.frontend.controller;

import be.acara.frontend.domain.User;
import be.acara.frontend.model.EventModelList;
import be.acara.frontend.model.UserModel;
import be.acara.frontend.service.EventService;
import be.acara.frontend.service.SecurityService;
import be.acara.frontend.service.UserService;
import be.acara.frontend.service.mapper.EventMapper;
import be.acara.frontend.service.mapper.UserMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final SecurityService securityService;
    private final UserMapper userMapper;
    private final EventService eventService;
    private final EventMapper eventMapper;
    
    private static final String ATTRIBUTE_USER_FORM = "userForm";
    private static final String REDIRECT_EVENTS = "redirect:/events";
    private static final String ATTRIBUTE_EDIT_USER_REDIRECT = "user/editUser";
    private static final String ATTRIBUTE_USER = "user";

    public UserController(UserService userService, SecurityService securityService, UserMapper userMapper, EventService eventService, EventMapper eventMapper) {
        this.userService = userService;
        this.securityService = securityService;
        this.userMapper = userMapper;
        this.eventService = eventService;
        this.eventMapper = eventMapper;
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
        return REDIRECT_EVENTS;
    }

    @GetMapping("/detail/{id}")
    @PreAuthorize("@securityService.hasUserId(authentication, #id) or hasRole('admin')")
    public String displayUser(@PathVariable("id") Long id, ModelMap model,
                              @RequestParam(name = "pageSubscribedEvents", defaultValue = "1", required = false) int pageSubscribedEvents,
                              @RequestParam(name = "sizeSubscribedEvents", defaultValue = "3", required = false) int sizeSubscribedEvents,
                              @RequestParam(name = "pageLikedEvents", defaultValue = "1", required = false) int pageLikedEvents,
                              @RequestParam(name = "sizeLikedEvents", defaultValue = "3", required = false) int sizeLikedEvents) {
        User user = userService.getUser(id);
        EventModelList subscribedEvents = eventMapper.eventDtoListToEventModelList(eventService.getEventsFromUser(id, pageSubscribedEvents - 1, sizeSubscribedEvents));
        addPageNumbers(subscribedEvents, model, "pageNumbersSubscribedEvents");
        EventModelList likedEvents = eventMapper.eventDtoListToEventModelList(eventService.getEventsThatUserLiked(id, pageLikedEvents - 1, sizeLikedEvents));
        addPageNumbers(likedEvents, model, "pageNumbersLikedEvents");
        model.addAttribute(ATTRIBUTE_USER, user);
        model.addAttribute("subscribedEvents", subscribedEvents);
        model.addAttribute("likedEvents", likedEvents);
        return "user/userDetails";
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityService.hasUserId(authentication, #id) or hasRole('admin')")
    public String displayEditUserForm(@PathVariable("id") Long id, Model model) {
        UserModel user = userMapper.userToUserModel(userService.getUser(id));
        model.addAttribute(ATTRIBUTE_USER_FORM, user);
        return ATTRIBUTE_EDIT_USER_REDIRECT;
    }

    @PostMapping("/{id}")
    @PreAuthorize("@securityService.hasUserId(authentication, #user.id) or hasRole('admin')")
    public String handleEditUserForm(@Valid @ModelAttribute(ATTRIBUTE_USER_FORM) UserModel user, BindingResult br) {
        if (br.hasErrors()) {
            return ATTRIBUTE_EDIT_USER_REDIRECT;
        }
        userService.editUser(user);
        return REDIRECT_EVENTS;
    }

    @PostMapping("/{location}/likes/{eventId}")
    public String likeOrDislikeEvent(@RequestParam("liked") boolean liked, @PathVariable("location") String location, @PathVariable("eventId") Long eventId) {
        if (liked) {
            userService.dislikeEvent(eventId);
        } else {
            userService.likeEvent(eventId);
        }
        String targetUrl = REDIRECT_EVENTS;
        if ("details".equals(location)) {
            targetUrl = "redirect:/events/detail/" + eventId;
        }
        return targetUrl;
    }

    @GetMapping("/profile")
    public String getProfile(Principal principal) {
        return String.format("forward:/users/detail/%d", userService.findByUsername(principal.getName()).getId());
    }

    private void addPageNumbers(EventModelList events, ModelMap modelMap, String attribute) {
        if (events.getTotalPages() == 1) {
            return;
        }
        modelMap.addAttribute(attribute, IntStream.rangeClosed(1, events.getTotalPages())
                .boxed()
                .collect(Collectors.toList()));
    }
}
