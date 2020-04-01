package be.acara.frontend.controller;

import be.acara.frontend.controller.dto.UserDto;
import be.acara.frontend.model.User;
import be.acara.frontend.service.UserFeignClient;
import be.acara.frontend.service.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserFeignClient userFeignClient;
    private final UserMapper mapper;

    @Autowired
    public UserController(UserFeignClient userFeignClient, UserMapper mapper) {
        this.userFeignClient = userFeignClient;
        this.mapper = mapper;
    }

    @GetMapping("/detail/{id}")
    public String displayEvent(@PathVariable("id") Long id, ModelMap model) {
        UserDto userDto = userFeignClient.getUserById(id);
        User user = mapper.map(userDto);
        model.addAttribute("user", user);
        return "userDetails";
    }
}
