package org.example;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException {
        ZipFile zipFile = new ZipFile();
        System.out.println( "Hello Zip World!" );
        File file = new File("src/main/resources/Readme.md");
        FileOutputStream fos = new FileOutputStream("src/main/resources/compressed.zip");
        byte[] bytes = zipFile.zipBytes(file.getName(), FileUtils.readFileToByteArray(file),file);

        // save file
        fos.write(bytes);
        fos.close();
    }
}
