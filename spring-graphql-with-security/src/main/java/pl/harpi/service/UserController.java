package pl.harpi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("users")
public class UserController {
    private final UserService userService;

    @PostMapping("/signin")
    public String signin(@RequestBody UserDto user) {
        return userService.signin(user.getUsername(), user.getPassword());
    }

    @PostMapping("/signup")
    public String signup(@RequestBody UserDto user) {
        return userService.signup(user);
    }
}

