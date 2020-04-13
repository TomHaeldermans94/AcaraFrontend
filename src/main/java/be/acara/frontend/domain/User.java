package be.acara.frontend.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "NotBlank.user.username")
    private String username;
    @NotBlank(message = "NotBlank.user.password")
    private String password;
    @Transient
    @NotBlank(message = "NotBlank.user.passwordConfirm")
    private String passwordConfirm;
    @ManyToMany
    private Set<Role> roles;
    @NotBlank(message = "NotBlank.user.firstName")
    private String firstName;
    @NotBlank(message = "NotBlank.user.lastName")
    private String lastName;
    private Boolean uniqueUsername = true;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId()) &&
                Objects.equals(getUsername(), user.getUsername()) &&
                Objects.equals(getPassword(), user.getPassword()) &&
                Objects.equals(getPasswordConfirm(), user.getPasswordConfirm()) &&
                Objects.equals(getFirstName(), user.getFirstName()) &&
                Objects.equals(getLastName(), user.getLastName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUsername(), getPassword(), getPasswordConfirm(), getFirstName(), getLastName());
    }
}

