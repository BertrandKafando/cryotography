package ma.enset;

import java.io.FileOutputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collection;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.itextpdf.forms.fields.PdfSignatureFormField;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.IExternalDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.PdfSigner.CryptoStandard;

public class HellWorldSign {

    public static final String KEYSTORE = "src/main/ressources/keyStore.p12";
    public static final String PASSWORD = "mypassword";
    public static final String SRC = "src/main/ressources/Hello.pdf";
    public static final String DEST = "src/main/ressources/res/hello_signed%s.pdf";
    public static final String temp = "src/main/ressources/tmp/tmp.pdf";

    void sign(String src, String dest,
            Certificate[] chain, PrivateKey pk, String digestAlgorithm, String provider,
            CryptoStandard subfilter, String reason, String location)
            throws GeneralSecurityException,  java.io.IOException {
        PdfReader reader = new PdfReader(src);
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), temp, new StampingProperties().useAppendMode());

        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance.setReason(reason);
        appearance.setLocation(location);
        appearance.setPageRect(new Rectangle(400, 200, 200, 100));
        appearance.setLayer2Text("This document was signed by Berto Specimen");
        signer.setFieldName("sig");
        signer.setCertificationLevel(PdfSigner.CERTIFIED_NO_CHANGES_ALLOWED);

        IExternalSignature pks = new PrivateKeySignature(pk, digestAlgorithm, provider);
        IExternalDigest digest = new BouncyCastleDigest();

        // Sign the document using the detached mode, CMS or CAdES equivalent.
        signer.signDetached(digest, pks, chain, null, null, null, 0, subfilter);

    }

    public static void main(String[] args) throws Exception {
        LoadKeystore loadKeystore = new LoadKeystore(KEYSTORE, PASSWORD);
        X509Certificate cert = loadKeystore.getCertificate("mycert");
        PrivateKey privateKey = (PrivateKey) loadKeystore.getPrivateKey("myalias", "mypassword");
        PublicKey publicKey = (PublicKey) loadKeystore.getPublicKey("mycert");
        Certificate[] chain = new Certificate[] { cert };
        Certificate[] chain2=loadKeystore.getCertificateChain("myalias");
        
        // Provider
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);

        // sign
        HellWorldSign app = new HellWorldSign();
        app.sign(SRC, String.format(DEST, 1), chain, privateKey, DigestAlgorithms.SHA256,
                provider.getName(), CryptoStandard.CMS, "Test 1", "Ghent");

        /* app.sign(SRC, String.format(DEST, 2), chain, privateKey, DigestAlgorithms.SHA512,
                provider.getName(), CryptoStandard.CMS, "Test 2", "Ghent");
        app.sign(SRC, String.format(DEST, 3), chain, privateKey, DigestAlgorithms.SHA256,
                provider.getName(), CryptoStandard.CADES, "Test 3", "Ghent");
        app.sign(SRC, String.format(DEST, 4), chain, privateKey, DigestAlgorithms.RIPEMD160,
                provider.getName(), CryptoStandard.CADES, "Test 4", "Ghent"); */
    }



}
