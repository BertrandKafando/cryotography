package es.kf.signapp.apis;

import es.kf.signapp.dtos.AddFormPayload;
import es.kf.signapp.external.signature.CreateEmptySignatureForm;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@RestController
@RequestMapping("/prepSign")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PrepSignApi {
    private  final CreateEmptySignatureForm createEmptySignatureForm;

    @PostMapping("/addform")
    public byte[] addform(@RequestBody AddFormPayload payload) throws IOException {
        String[] strings = payload.getBase64().split(",");
        byte[]  fileContent = Base64.getDecoder().decode(strings[1]);
        return createEmptySignatureForm.EmptyForm(fileContent, payload.getName(), payload.getReason(), payload.getPageNumb(), payload.getLocation());
    }
}
