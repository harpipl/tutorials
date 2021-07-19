package pl.harpi.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.harpi.model.Role;
import pl.harpi.model.User;
import pl.harpi.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyUserDetails implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User '" + username + "' not found");
        } else {
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.get().getUsername())
                    .password(user.get().getPassword())
                    .authorities(Role.ROLE_ADMIN)
                    .accountExpired(false)
                    .accountLocked(user.get().isLocked())
                    .credentialsExpired(false)
                    .disabled(false)
                    .build();
        }
    }
}
