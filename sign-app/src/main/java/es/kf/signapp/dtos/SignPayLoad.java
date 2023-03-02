package es.kf.signapp.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SignPayLoad {
    private String pdfBytes;
    private String name="John Doe";
    private String reason="I agree";
    private String location="Morocco";
    private List<Coordinate> coordinates;
}
