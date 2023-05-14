package es.kf.signapp.apis;

import es.kf.signapp.dtos.Login;
import es.kf.signapp.dtos.UserDTO;
import es.kf.signapp.model.User;
import es.kf.signapp.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserApi {

    private final UserService userService;

    @PostMapping("/register")
    public User register( @RequestBody UserDTO user) {
        return userService.saveUser(user);
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }

    @GetMapping("/all")
    public Iterable<User> all() {
        return userService.getAllUsers();
    }


}
