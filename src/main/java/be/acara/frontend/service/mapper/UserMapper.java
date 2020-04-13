package be.acara.frontend.service.mapper;

import be.acara.frontend.controller.dto.UserDto;
import be.acara.frontend.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User map(UserDto userDto) {
        return User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .id(userDto.getId())
                .build();
    }

    public be.acara.frontend.model.User mapUserForEdit(UserDto userDto) {
        return be.acara.frontend.model.User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .id(userDto.getId())
                .build();
    }

    public UserDto map(User user){
        return UserDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .password(user.getPassword())
                .id(user.getId())
                .build();
    }

    public UserDto mapUserForEdit(be.acara.frontend.model.User user){
        return UserDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .password(user.getPassword())
                .id(user.getId())
                .build();
    }
}
