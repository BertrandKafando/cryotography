package es.kf.signservlet;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.*;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
public class SignImp {
    public static final String KEYSTORE = "src/main/resources/keyStore.p12";
    public static final String PASSWORD = "mypassword";
    public static final String SRC = "src/main/resources/Hello.pdf";
    public static final String DEST = "src/main/resources/res/hello_signed%s.pdf";
    private PrivateKey pk;
    private Certificate[] chain;

    public SignImp()  {

    }

    public SignImp(PrivateKey pk, Certificate[] chain) {
        this.pk = pk;
        this.chain = chain;
    }

    public static void main(String[] args) throws GeneralSecurityException, IOException {

    }

    public byte[] signDoc(InputStream pdf, String reason, String location)
            throws GeneralSecurityException, java.io.IOException {
        PdfReader reader = new PdfReader(pdf);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfSigner signer = new PdfSigner(reader, baos, new StampingProperties().useAppendMode());

        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance.setReason(reason);
        appearance.setLocation(location);
        appearance.setPageRect(new Rectangle(400, 200, 200, 100));
        appearance.setLayer2Text("This document was signed by Berto Specimen");
        signer.setFieldName("sig");
        signer.setCertificationLevel(PdfSigner.CERTIFIED_NO_CHANGES_ALLOWED);

        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, null);
        IExternalDigest digest = new BouncyCastleDigest();

        // Sign the document using the detached mode, CMS or CAdES equivalent.
        signer.signDetached(digest, pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CMS);
        System.out.println("Signed: " + SRC);
        return baos.toByteArray();
    }
}
