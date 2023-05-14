package es.kf.signapp.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private String username;
    private String login;
    private String email;
    private String password;
    private String role;

}
