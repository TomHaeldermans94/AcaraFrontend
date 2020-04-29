package be.acara.frontend.util;

import be.acara.frontend.controller.dto.UserDto;
import be.acara.frontend.domain.Role;
import be.acara.frontend.domain.User;
import be.acara.frontend.model.UserModel;

import java.util.Set;

public class UserUtil {

    public static UserDto firstUserDto() {
        return UserDto.builder()
                .id(1L)
                .firstName("firstName")
                .lastName("lastName")
                .email("email@email.local")
                .build();
    }

    public static UserModel firstUser() {
        return UserModel.builder()
                .id(1L)
                .firstName("firstName")
                .lastName("lastName")
                .username("username")
                .password("password")
                .passwordConfirm("password")
                .email("email@email.local")
                .emailConfirm("email@email.local")
                .build();
    }

    public static User firstUserDomain() {
        Role role = new Role();
        role.setName("USER");
        role.setId(1L);
        return User.builder()
                .id(1L)
                .firstName("firstName")
                .lastName("lastName")
                .username("username")
                .password("password")
                .passwordConfirm("password")
                .email("email@email.local")
                .roles(Set.of(role))
                .build();
    }
}
