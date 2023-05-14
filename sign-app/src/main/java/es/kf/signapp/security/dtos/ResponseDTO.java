package es.kf.signapp.security.dtos;

import es.kf.signapp.model.User;
import lombok.Data;

@Data
public class ResponseDTO {
    private User user;
    private String accessToken;
    private String refreshToken;
}
