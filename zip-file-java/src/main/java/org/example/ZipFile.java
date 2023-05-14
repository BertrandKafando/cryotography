package org.example;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFile {
// https://www.baeldung.com/java-compress-and-uncompress
    public  byte[] zipBytes(String filename, byte[] input, File in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        ZipEntry entry = new ZipEntry(filename);
        entry.setSize(input.length);
        zos.putNextEntry(entry);
        FileInputStream fis = new FileInputStream(in);
        byte[] bytes = new byte[1024];
        int length;
        while((length = fis.read(bytes)) >= 0) {
            zos.write(bytes, 0, length);
        }
       // zos.write(input);
        zos.closeEntry();
        zos.close();
        return baos.toByteArray();
    }



}
