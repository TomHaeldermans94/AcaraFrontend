package be.acara.frontend.service.mapper;

import be.acara.frontend.controller.dto.UserDto;
import be.acara.frontend.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final EventMapper eventMapper;

    public UserMapper(EventMapper eventMapper) {
        this.eventMapper = eventMapper;
    }

    public User map(UserDto userDto) {
        return User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .id(userDto.getId())
                .build();
    }

    public UserDto map(User user){
        return UserDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .id(user.getId())
                .build();
    }
}
