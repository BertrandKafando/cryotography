package es.kf.signapp.dtos;

import es.kf.signapp.model.User;
import es.kf.signapp.model.Workflow;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class DocumentDTO {
    private Long id;
    private String path;
    private String filename;
    private  String base64;
    private UserDTO sender;
    private String metadata;
    private String notes;
    private List<WorkflowDTO> workflows;
}
