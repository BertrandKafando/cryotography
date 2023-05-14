package es.kf.signapp.dtos;


import lombok.Data;

@Data
public class SignResponse {
    private String pdfBytes;
    private String fileName="signed.pdf";
    private DocumentDTO document;
    private WorkflowDTO workflow;
}
