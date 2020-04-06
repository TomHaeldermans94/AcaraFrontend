package be.acara.frontend.util;

import be.acara.frontend.controller.dto.UserDto;
import be.acara.frontend.model.Event;
import be.acara.frontend.model.User;

import java.util.Set;

public class UserUtil {

    public static UserDto firstUserDto() {
        return UserDto.builder()
                .id(1L)
                .firstName("firstName")
                .lastName("lastName")
                .build();
    }

    public static User firstUser() {
        Set<Event> events = EventUtil.createSetOfEventsOfSize3();
        return User.builder()
                .id(1L)
                .firstName("firstName")
                .lastName("lastName")
                .userName("userName")
                .password("password")
                .events(events)
                .build();
    }
}
