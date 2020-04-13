package be.acara.frontend.util;

import be.acara.frontend.controller.dto.UserDto;
import be.acara.frontend.model.User;

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
                .passwordConfirm("password")
                .build();
    }

    public static be.acara.frontend.domain.User firstUserDomain() {
        return be.acara.frontend.domain.User.builder()
                .id(1L)
                .firstName("firstName")
                .lastName("lastName")
                .username("username")
                .password("password")
                .passwordConfirm("password")
                .build();
    }
}
