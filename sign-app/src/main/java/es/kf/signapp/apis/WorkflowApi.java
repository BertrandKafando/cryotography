package es.kf.signapp.apis;

import es.kf.signapp.dtos.GetWorklfowDTO;
import es.kf.signapp.model.Workflow;
import es.kf.signapp.model.WorkflowStatus;
import es.kf.signapp.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/documents/workflows")
public class WorkflowApi {
    private  final WorkflowService workflowService;

     @GetMapping("/all")
    public List<Workflow> all() {
         return workflowService.getAllWorkflows();
     }

     @GetMapping("/document/{documentId}")
    public List<Workflow> getDocumentWorkflows(@PathVariable  Long documentId) {
         return workflowService.getWorkflowsByDocumentId(documentId);
     }


     @GetMapping("/user/{userId}")
    public List<GetWorklfowDTO> getWorklfowDTOS(@PathVariable Long userId) {
     return workflowService.getWorkflowsByUserIdAndStatus(userId, WorkflowStatus.PENDING);
     }

     @GetMapping("/{workflowId}")
    public Workflow getWorkflow(@PathVariable Long workflowId) {
         return workflowService.getWorkflowById(workflowId);
     }




}
