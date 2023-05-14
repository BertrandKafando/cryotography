package es.kf.signapp.service;
import es.kf.signapp.dtos.UserDTO;
import es.kf.signapp.model.SignMode;
import es.kf.signapp.model.User;
import es.kf.signapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private  final UserRepository userRepository;

    public User saveUser(UserDTO user) {
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setLogin(user.getLogin());
        newUser.setEmail(user.getEmail());
       return userRepository.save(newUser);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }


    public User getUser(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
