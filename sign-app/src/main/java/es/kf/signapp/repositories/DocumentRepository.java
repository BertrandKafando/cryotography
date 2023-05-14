package es.kf.signapp.repositories;

import es.kf.signapp.model.Document;
import es.kf.signapp.model.User;
import es.kf.signapp.model.WorkflowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findBySenderId(Long id);

    @Query("SELECT d FROM Document d WHERE NOT EXISTS (SELECT w FROM d.workflows w WHERE w.status <> 'Ok') AND d.sender.id=:senderId" )
    List<Document> findAllDocumentsWithOkWorkflowStatus( @Param("senderId") Long senderId);
List<Document> findBySenderIdAndWorkflows_Status(Long sender, WorkflowStatus status);
}
