package es.kf.signapp.repositories;

import es.kf.signapp.model.Document;
import es.kf.signapp.model.User;
import es.kf.signapp.model.Workflow;
import es.kf.signapp.model.WorkflowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Long> {

    List<Workflow> findByDocumentId(Long id);

    List<Workflow> findByUserToSignId(Long id);


    List<Workflow> findByUserToSignIdAndStatus(Long id, WorkflowStatus status);
}
