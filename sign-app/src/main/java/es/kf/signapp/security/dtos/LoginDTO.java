package es.kf.signapp.security.dtos;

import lombok.Data;

@Data
public class LoginDTO {
    private String grantType;
    private String username;
    private String password;
    private boolean withRefreshToken;
    private String refreshToken;
}
