package be.acara.frontend.controller;

import be.acara.frontend.model.Event;
import be.acara.frontend.model.User;
import be.acara.frontend.service.EventFeignClient;
import be.acara.frontend.service.UserFeignClient;
import be.acara.frontend.service.mapper.EventMapper;
import be.acara.frontend.service.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserFeignClient userFeignClient;
    private final EventFeignClient eventFeignClient;
    private final UserMapper userMapper;
    private final EventMapper eventMapper;

    @Autowired
    public UserController(UserFeignClient userFeignClient, EventFeignClient eventFeignClient, UserMapper mapper, EventMapper eventMapper) {
        this.userFeignClient = userFeignClient;
        this.eventFeignClient = eventFeignClient;
        this.userMapper = mapper;
        this.eventMapper = eventMapper;
    }

    @GetMapping("/detail/{id}")
    public String displayEvent(@PathVariable("id") Long id, ModelMap model) {
        User user = userMapper.map(userFeignClient.getUserById(id));
        List<Event> events = eventMapper.map(eventFeignClient.getAllEventsFromSelectedUser(id)).getEventList();
        model.addAttribute("user", user);
        model.addAttribute("events", events);
        return "userDetails";
    }
}
