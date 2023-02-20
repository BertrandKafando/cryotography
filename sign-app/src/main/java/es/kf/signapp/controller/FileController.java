package es.kf.signapp.controller;

import es.kf.signapp.service.HandleFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class FileController {

    HandleFile handleFile;

    public FileController() throws IOException {
        this.handleFile = new HandleFile(PrepareFormController.SRC);
    }

    @GetMapping("/file")
    public byte[] getFile() throws IOException {
        return handleFile.getFileinBytes();
    }
}

