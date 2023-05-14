package es.kf.signapp.dtos;

import es.kf.signapp.model.Document;
import es.kf.signapp.model.SignMode;
import es.kf.signapp.model.SignaturePosition;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class GetWorklfowDTO {
    private Long id;
    private String status;
    private String notes;
    private Long userId;
    private int order;
    private LocalDate processDate;
    private SignMode signatureMode;
    private List<SignaturePosition> signaturePosition;
    private Document document;
}
