package es.kf.signapp.apis;

import es.kf.signapp.dtos.DocumentDTO;
import es.kf.signapp.dtos.SignRequestPayLoad;
import es.kf.signapp.dtos.SignResponse;
import es.kf.signapp.model.Document;
import es.kf.signapp.service.CreateVisibleSignature2;
import es.kf.signapp.service.DocumentService;
import es.kf.signapp.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/documents")
public class DocumentApi {
    private final DocumentService documentService;

    private final WorkflowService workflowService;
    public static final String SIGN = "src/main/resources/sign";
    private final CreateVisibleSignature2 createVisibleSignature2;


    @PostMapping("/add")
    public Document addDocument(@RequestBody DocumentDTO document) {
        return documentService.createDocument(document);
    }

    @GetMapping("/all/{userId}")
    public Iterable<Document> getUsersDocuments(@PathVariable Long userId) {
        return documentService.getAllUsersDocuments( userId);
    }

    @GetMapping("/all")
    public Iterable<Document> getAllDocuments() {
        return documentService.getAllDocuments();
    }

    @GetMapping("/{documentId}")
    public Document getDocument(@PathVariable Long documentId) {
        return documentService.getDocument(documentId);
    }

    @GetMapping("allSign/{userId}")
    public Iterable<Document> getAllSign(@PathVariable Long userId) {
        return documentService.getAllSign(userId);
    }

   @PostMapping("/sign")
   public SignResponse fromSvelte2(@RequestBody SignRequestPayLoad payload) throws IOException {
      return  documentService.signDocument(payload);
   }

   @GetMapping("/allInProgress/{userId}")
    public Iterable<Document> getAllInProgress(@PathVariable Long userId) {
         return documentService.getAllInProgress(userId);
    }

}
