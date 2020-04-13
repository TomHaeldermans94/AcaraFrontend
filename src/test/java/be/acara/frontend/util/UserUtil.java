package be.acara.frontend.util;

import be.acara.frontend.controller.dto.UserDto;
import be.acara.frontend.domain.User;

public class UserUtil {

    public static UserDto firstUserDto() {
        return UserDto.builder()
                .id(1L)
                .firstName("firstName")
                .lastName("lastName")
                .build();
    }

    public static User firstUser() {
        return User.builder()
                .id(1L)
                .firstName("firstName")
                .lastName("lastName")
                .username("username")
                .password("password")
                .build();
    }
}
