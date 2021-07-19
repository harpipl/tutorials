package pl.harpi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.harpi.model.User;
import pl.harpi.repository.UserRepository;
import pl.harpi.security.CustomException;
import pl.harpi.security.JwtTokenProvider;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public String signin(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            return jwtTokenProvider.createToken(username, new ArrayList<>());
        } catch (AuthenticationException e) {
            throw new CustomException("Invalid username/password supplied - " + e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public String signup(UserDto user) {
        userRepository.save(User.builder()
                .username(user.getUsername())
                .password(passwordEncoder.encode(user.getPassword()))
                .build());

        return signin(user.getUsername(), user.getPassword());
    }
}

