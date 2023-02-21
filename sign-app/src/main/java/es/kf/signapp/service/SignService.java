package es.kf.signapp.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;


public class SignService {

    KeyStore keystore;
    char[] password;
    CreateVisibleSignature2 signInterface;
    public static final String KEYSTORE = "src/main/resources/keyStore.p12";
    public static final String PASSWORD = "mypassword";
    public static final String SRC = "src/main/resources/Hello.pdf";
    public static final String DEST = "src/main/resources/res/";
    public static final String IMG = "src/main/resources/imgs/es.png";

    public  SignService() throws UnrecoverableKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException {
        LoadKeytore();
        signInterface = new CreateVisibleSignature2(keystore,password);
    }


    public byte [] addSignatureRequest(byte[] file) throws IOException {
        PDDocument doc = PDDocument.load(file);
        //create a rectangle for the signature
        PDRectangle rectangle = new PDRectangle(100, 100, 200, 100);
        PDSignature signature = new PDSignature();
        signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
        signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
        signature.setName("Test");
        signature.setReason("I'm testing");
        signature.setLocation("Foobar");
        InputStream st=   signInterface.createVisualSignatureTemplate(doc,1,rectangle, signature);
        doc.save(DEST+"hello_.pdf");
        doc.close();
        return st.readAllBytes();
    }







    public  void LoadKeytore() throws KeyStoreException {
       keystore = KeyStore.getInstance("PKCS12");
        char[] password = PASSWORD.toCharArray(); // TODO use Java 6 java.io.Console.readPassword
        try (InputStream is = new FileInputStream(KEYSTORE))
        {
            keystore.load(is, password);
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new RuntimeException(e);
        }

    }
}
