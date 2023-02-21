package es.kf.signapp.dtos;

import lombok.Data;

@Data
public class AddFormPayload {
    private String base64;
    private String name;
    String reason;
    String location;
    int pageNumb;
}
