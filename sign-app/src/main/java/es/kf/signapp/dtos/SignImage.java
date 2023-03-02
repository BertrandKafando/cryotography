package es.kf.signapp.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignImage {
    private String base64;
    private boolean isDrawImage;
    private Coordinate coordinate;
}
