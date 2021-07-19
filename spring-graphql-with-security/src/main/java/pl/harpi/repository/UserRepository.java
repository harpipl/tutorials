package pl.harpi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.harpi.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
