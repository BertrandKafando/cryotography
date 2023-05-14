package es.kf.signapp.apis;

import es.kf.signapp.dtos.SignPayLoad;
import es.kf.signapp.dtos.SignRequestPayLoad;
import es.kf.signapp.dtos.SignResponse;
import es.kf.signapp.service.CreateVisibleSignature2;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/sign")
@RequiredArgsConstructor
public class SignApi {
    private final    CreateVisibleSignature2 createVisibleSignature2;
    public static final String DEST = "src/main/resources/res";
    public static final String SIGN = "src/main/resources/sign";
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
        byte[]  result = this.createVisibleSignature2.signPDF(fileContent, signedDocumentFile, humanRect, tsaUrl, "Signature1",payload.getCoordinates().get(0).getPageIndex());
         // save the signed document to a file

        SignResponse signResponse = new SignResponse();
        signResponse.setPdfBytes(Base64.getEncoder().encodeToString(result));
        return signResponse ;
    }


    @PostMapping("/fromSvelte2")
    public SignResponse fromSvelte2(@RequestBody SignRequestPayLoad payload) throws IOException {
        //  this.createVisibleSignature2.usage();
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
        return signResponse ;
    }
}
