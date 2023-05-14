package es.kf.signapp.service;

import es.kf.signapp.dtos.GetWorklfowDTO;
import es.kf.signapp.mappers.WorkflowMapper;
import es.kf.signapp.model.Document;
import es.kf.signapp.model.Workflow;
import es.kf.signapp.model.WorkflowStatus;
import es.kf.signapp.repositories.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private  final WorkflowRepository workflowRepository;
    private final WorkflowMapper workflowMapper;

    public Workflow  createWorkflow(Workflow workflow) {
        return workflowRepository.save(workflow);
    }



    public Workflow getWorkflowById(Long id) {
        return workflowRepository.findById(id).orElse(null);
    }

    public List<Workflow> getAllWorkflows() {
        return workflowRepository.findAll();
    }

    public List<Workflow> getWorkflowsByDocumentId(Long id) {
        return workflowRepository.findByDocumentId(id);
    }

    public List<GetWorklfowDTO> getWorkflowsByUserId(Long id) {
        List<Workflow> workflows = workflowRepository.findByUserToSignId(id);
        return  workflows.stream().map(workflowMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<GetWorklfowDTO>getWorkflowsByUserIdAndStatus(Long id, WorkflowStatus status) {
        List<Workflow> workflows = workflowRepository.findByUserToSignIdAndStatus(id, status);
        return  workflows.stream().map(workflowMapper::toDto)
                .collect(Collectors.toList());
    }


    public Workflow updateWorkflow(Workflow workflow) {
       return   workflowRepository.save(workflow);
    }
}
