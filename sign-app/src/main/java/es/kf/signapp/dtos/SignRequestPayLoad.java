package es.kf.signapp.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignRequestPayLoad {
    private String pdfBytes;
    private String fileName;
    private DocumentDTO document;
    private SignImage signImage;
    private WorkflowDTO workflow;
}
