package es.kf.signapp.service;

import es.kf.signapp.dtos.DocumentDTO;
import es.kf.signapp.dtos.SignRequestPayLoad;
import es.kf.signapp.dtos.SignResponse;
import es.kf.signapp.mappers.DocumentMapper;
import es.kf.signapp.mappers.WorkflowMapper;
import es.kf.signapp.model.Document;
import es.kf.signapp.model.User;
import es.kf.signapp.model.Workflow;
import es.kf.signapp.model.WorkflowStatus;
import es.kf.signapp.repositories.DocumentRepository;
import es.kf.signapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {
    public static final String DEST = "src/main/resources/src";
    private final DocumentRepository documentRepository;

    private final  WorkflowService workflowService;

    private final UserRepository userRepository;
    private static final String SIGN = "src/main/resources/sign";
    private final CreateVisibleSignature2 createVisibleSignature2;
    private  final WorkflowMapper workflowMapper;
    private final DocumentMapper documentMapper;

  private Document  documentDTODocument (DocumentDTO documentDTO) {
    Document document = new Document();
    document.setPath(documentDTO.getPath());
    document.setFilename(documentDTO.getFilename());
    User user = userRepository.findByEmail(documentDTO.getSender().getEmail());
    document.setSender(user);
    document.setMetadata(documentDTO.getMetadata());
    document.setNotes(documentDTO.getNotes());
    return document;
  }
    public Document createDocument(DocumentDTO documentDTO)  {

        Document document = documentDTODocument(documentDTO);
        try {
            String[] strings = document.getPath().split(",");
            byte[] fileContent = Base64.getDecoder().decode(strings[1]);
            File signedDocumentFile = new File(DEST + "/tosign.pdf");
            FileUtils.writeByteArrayToFile(signedDocumentFile, fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        document.setPath(DEST + "/tosign.pdf");

        // create a list of Workflow entities
        List<Workflow> workflows = new ArrayList<>();
        documentDTO.getWorkflows().forEach(workflowDTO -> {
            Workflow workflow = new Workflow();
            workflow.setSignatureMode(workflowDTO.getSignatureMode());
            workflow.setUserToSign(userRepository.findById(workflowDTO.getUserId()).orElse(null));
            workflow.setStatus(WorkflowStatus.PENDING);
            workflow.setSignaturePosition(workflowDTO.getSignaturePosition());
            workflow.setDocument(document); // set the Document reference
            workflows.add(workflow); // add the Workflow to the list
        });

        Document savedDocument = documentRepository.save(document); // save the Document

        // save all the Workflow entities after the Document has been persisted
        workflows.forEach(workflowService::createWorkflow);

        return savedDocument;
    }


    public List<Document> getAllUsersDocuments(Long id) {
    List<Document> docs= documentRepository.findBySenderId(id);
    docs.forEach(doc -> {
        getBase64(doc);
    });
    return docs;
    }

    private static void getBase64(Document doc) {
        try {
            byte[] fileContent = FileUtils.readFileToByteArray(new File(doc.getPath()));
            String encodedString = Base64.getEncoder().encodeToString(fileContent);
            doc.setBase64(encodedString);
        } catch (IOException e) {
            doc.setBase64("file/error");
        }
    }


    public List<Document> getAllDocuments() {
      List <Document> docs= documentRepository.findAll();
        docs.forEach(doc -> {
            getBase64(doc);
        });
        return docs;
    }

    public Document getDocument(Long documentId) {
        Document doc = documentRepository.findById(documentId).orElse(null);
        getBase64(doc);
        return doc;
    }

    public SignResponse signDocument (SignRequestPayLoad payload) throws IOException {
        String[] strings = payload.getPdfBytes().split(",");
        byte[]  fileContent = Base64.getDecoder().decode(strings[1]);
        File signedDocumentFile = new File(SIGN + "/signed"+payload.getFileName() +".pdf ");
        Rectangle2D humanRect = new Rectangle2D.Float(payload.getSignImage().getCoordinate().getX(), payload.getSignImage().getCoordinate().getY(), 2*payload.getSignImage().getCoordinate().getWidth(), payload.getSignImage().getCoordinate().getHeight());
        String tsaUrl = null;
        // image;
        String[] strings2 = payload.getSignImage().getBase64().split(",");
        byte[]  image = Base64.getDecoder().decode(strings2[1]);
        this.createVisibleSignature2.setImageFile(image);
        //
        byte[]  result = this.createVisibleSignature2.signPDF(fileContent, signedDocumentFile, humanRect, tsaUrl, "Signature4",payload.getSignImage().getCoordinate().getPageIndex());

        SignResponse signResponse = new SignResponse();
        signResponse.setPdfBytes(Base64.getEncoder().encodeToString(result));

        // update workflow
        Workflow workflow = workflowService.getWorkflowById(payload.getWorkflow().getId());
        workflow.setStatus(WorkflowStatus.OK);
        signResponse.setWorkflow(workflowMapper.toDto2(workflowService.updateWorkflow(workflow)));

        // update document
        Document document = documentRepository.findById(payload.getDocument().getId()).orElse(null);
        document.setPath(SIGN + "/signed"+payload.getFileName() +".pdf ");
        signResponse.setDocument(documentMapper.toDto(document));
        documentRepository.save(document);

        return signResponse;
    }

    public Iterable<Document> getAllSign(Long userId) {
    return documentRepository.findAllDocumentsWithOkWorkflowStatus(userId);
    }

    public Iterable<Document> getAllInProgress(Long userId) {
    return documentRepository.findBySenderIdAndWorkflows_Status(userId,WorkflowStatus.OK);
    }
}
