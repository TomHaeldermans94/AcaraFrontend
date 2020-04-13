package be.acara.frontend.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    @NotBlank(message = "NotBlank.user.username")
    private String username;
    @NotBlank(message = "NotBlank.user.firstName")
    private String firstName;
    @NotBlank(message = "NotBlank.user.lastName")
    private String lastName;
    private Set<Event> events;
    @NotBlank(message = "NotBlank.user.password")
    private String password;
    @NotBlank(message = "NotBlank.user.passwordConfirm")
    private String passwordConfirm;
    private Boolean uniqueUsername = true;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId()) &&
                Objects.equals(getUsername(), user.getUsername()) &&
                Objects.equals(getFirstName(), user.getFirstName()) &&
                Objects.equals(getLastName(), user.getLastName()) &&
                Objects.equals(getEvents(), user.getEvents()) &&
                Objects.equals(getPassword(), user.getPassword()) &&
                Objects.equals(getPasswordConfirm(), user.getPasswordConfirm()) &&
                Objects.equals(getUniqueUsername(), user.getUniqueUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUsername(), getFirstName(), getLastName(), getEvents(), getPassword(), getPasswordConfirm(), getUniqueUsername());
    }
}
