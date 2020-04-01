package be.acara.frontend.util;

import be.acara.frontend.controller.dto.UserDto;
import be.acara.frontend.model.Event;
import be.acara.frontend.model.User;

import java.util.List;

public class UserUtil {

    public static UserDto firstUserDto() {
        return UserDto.builder()
                .id(1L)
                .firstName("firstName")
                .lastName("lastName")
                .build();
    }

    public static User firstUser() {
        List<Event> events = EventUtil.createListsOfEventsOfSize3();
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
