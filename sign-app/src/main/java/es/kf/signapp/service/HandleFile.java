package es.kf.signapp.service;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;


public class HandleFile {

    private File file;
    private byte[] fileBytes;
    private byte[]  outputBytes;

    public HandleFile (String path) throws IOException {
        this.file = new File(path);
        this.fileBytes = new byte[(int) this.file.length()];
        this.fileBytes = new FileInputStream(this.file).readAllBytes();
    }

     public URL getFileinUrl() throws MalformedURLException {
        return this.file.toURI().toURL();
    }

    public byte[] getFileinBytes() {
        return  this.fileBytes;
    }

    public  File getFile() {
        return this.file;
    }




}
