package es.kf.signapp.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignRequestPayLoad {
    private String pdfBytes;
    private String fileName;
    private SignImage signImage;
}
