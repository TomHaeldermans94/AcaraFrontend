package be.acara.frontend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserModel {
    private Long id;
    private String firstName;
    private String lastName;
    private Set<EventModel> events;
    private String username;
    private String password;
    private String passwordConfirm;
}
