package be.acara.frontend.model;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private Set<Event> events;
    private String password;
    private String passwordConfirm;
}
