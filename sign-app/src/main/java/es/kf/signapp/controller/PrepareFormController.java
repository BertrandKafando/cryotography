package es.kf.signapp.controller;

import es.kf.signapp.external.signature.CreateEmptySignatureForm;
import es.kf.signapp.service.HandleFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;

//@Controller
@RequiredArgsConstructor
public class PrepareFormController {
    public static final String SRC = "src/main/resources/Hello.pdf";

    public  final CreateEmptySignatureForm createEmptySignatureForm;

    @GetMapping("/prepDoc")
    public String signDoc(Model model) throws IOException {
        HandleFile file = new HandleFile(SRC);
        model.addAttribute("message", "Upload a document to sign");
        model.addAttribute("signDocName", "Document");
        model.addAttribute("baseUrl", "http://localhost:8082/file");
        model.addAttribute("fileurl", file.getFileinBytes());

        return "prepDoc";
    }

    @GetMapping("/addform")
    public String addform(Model model) throws IOException {
        HandleFile file = new HandleFile(SRC);
        this.createEmptySignatureForm.createEmptySignatureForm(SRC);
        model.addAttribute("message", "Upload a document to sign");
        return "prepDoc";
    }

}
