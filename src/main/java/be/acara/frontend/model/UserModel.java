package be.acara.frontend.model;

import be.acara.frontend.validators.FieldsValueMatch;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldsValueMatch.List({
        @FieldsValueMatch(
                field = "password",
                fieldMatch = "passwordConfirm",
                message = "Passwords do not match!"
        ),
        @FieldsValueMatch(
                field = "email",
                fieldMatch = "emailConfirm",
                message = "E-mails do not match!"
        )
})
public class UserModel {
    private Long id;
    @NotBlank(message = "NotBlank.user.username")
    private String username;
    @NotBlank(message = "NotBlank.user.firstName")
    private String firstName;
    @NotBlank(message = "NotBlank.user.lastName")
    private String lastName;
    private Set<EventModel> events;
    @NotBlank(message = "NotBlank.user.password")
    private String password;
    @NotBlank(message = "NotBlank.user.passwordConfirm")
    private String passwordConfirm;
    @NotBlank(message = "NotBlank.user.email")
    @Email
    private String email;
    @NotBlank(message = "NotBlank.user.emailConfirm")
    @Email
    private String emailConfirm;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserModel)) return false;
        UserModel user = (UserModel) o;
        return Objects.equals(getId(), user.getId()) &&
                Objects.equals(getUsername(), user.getUsername()) &&
                Objects.equals(getFirstName(), user.getFirstName()) &&
                Objects.equals(getLastName(), user.getLastName()) &&
                Objects.equals(getEvents(), user.getEvents()) &&
                Objects.equals(getPassword(), user.getPassword()) &&
                Objects.equals(getPasswordConfirm(), user.getPasswordConfirm());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUsername(), getFirstName(), getLastName(), getEvents(), getPassword(), getPasswordConfirm());
    }
}
