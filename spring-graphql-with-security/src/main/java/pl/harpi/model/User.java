package pl.harpi.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.Size;

import java.util.List;

import static pl.harpi.model.JPAConstants.*;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@Table(name = TABLE_USER)
public class User extends AbstractPersistable<Long> {
    @Size(min = USERNAME_MIN_LENGTH, max = USERNAME_LENGTH, message = USERNAME_MESSAGE)
    @Column(unique = true, nullable = false, name = COLUMN_USERNAME, length = USERNAME_LENGTH)
    private String username;

    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_LENGTH, message = PASSWORD_MESSAGE)
    @Column(name = COLUMN_PASSWORD, length = PASSWORD_LENGTH)
    private String password;

    @Column(name = COLUMN_LOCKED)
    private boolean locked;

    @CollectionTable(name = TABLE_USER_ROLE)
    @ElementCollection(fetch = FetchType.EAGER)
    List<Role> roles;
}
