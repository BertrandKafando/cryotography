package es.kf.signapp.apis;

import es.kf.signapp.dtos.SignPayLoad;
import es.kf.signapp.dtos.SignResponse;
import es.kf.signapp.service.CreateVisibleSignature2;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

@RestController
@CrossOrigin("*")
@RequestMapping("/sign")
@RequiredArgsConstructor
public class SignApi {
    private final    CreateVisibleSignature2 createVisibleSignature2;
    public static final String DEST = "src/main/resources/res";
    @GetMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @PostMapping("/fromSvelte")
    public SignResponse fromSvelte(@RequestBody SignPayLoad payload) throws IOException {
      //  this.createVisibleSignature2.usage();
        String[] strings = payload.getPdfBytes().split(",");
        byte[]  fileContent = Base64.getDecoder().decode(strings[1]);
        File signedDocumentFile = new File(DEST + "/signedDocument.pdf");
        Rectangle2D humanRect = new Rectangle2D.Float(payload.getCoordinates().get(0).getX(), payload.getCoordinates().get(0).getY(), payload.getCoordinates().get(0).getWidth(), payload.getCoordinates().get(0).getHeight());
        String tsaUrl = null;
        byte[]  result = this.createVisibleSignature2.signPDF(fileContent, signedDocumentFile, humanRect, tsaUrl, "Signature1");

        SignResponse signResponse = new SignResponse();
        signResponse.setPdfBytes(Base64.getEncoder().encodeToString(result));
        return signResponse ;
    }
}
