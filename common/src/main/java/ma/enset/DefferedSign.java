package ma.enset;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.ExternalBlankSignatureContainer;
import com.itextpdf.signatures.IExternalSignatureContainer;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;

public class DefferedSign {

    public static final String DEST = "src/main/ressources/servlet/";
    public static final String SRC = "src/main/ressources/Hello.pdf";
    public static final String TEMP = "src/main/ressources/tmp/hello_empty_sig.pdf";
    public static final String KEYSTORE = "src/main/ressources/keyStore.p12";
    public static final String PASSWORD = "mypassword";

    public static final String[] RESULT_FILES = new String[] {
            "hello_sig_ok.pdf"
    };

    public static void main(String[] args) throws Exception {

        BouncyCastleProvider providerBC = new BouncyCastleProvider();
        Security.addProvider(providerBC);
        LoadKeystore loadKeystore = new LoadKeystore(KEYSTORE, PASSWORD);
        X509Certificate cert = loadKeystore.getCertificate("mycert");
        PrivateKey pk = (PrivateKey) loadKeystore.getPrivateKey("myalias", "mypassword");
        PublicKey publicKey = (PublicKey) loadKeystore.getPublicKey("mycert");
        Certificate[] chain = new Certificate[] { cert };

        DefferedSign app = new DefferedSign();
        app.emptySignature(SRC, TEMP, "sig", chain);
        app.createSignature(TEMP, DEST + RESULT_FILES[0], "sig", pk, chain);
    }

    public void emptySignature(String src, String dest, String fieldname, Certificate[] chain)
            throws IOException, GeneralSecurityException {
        PdfReader reader = new PdfReader(src);
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), new StampingProperties());
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance
                .setPageRect(new Rectangle(36, 748, 200, 100))
                .setPageNumber(1)
                .setCertificate(chain[0]);
        signer.setFieldName(fieldname);

        /*
         * ExternalBlankSignatureContainer constructor will create the PdfDictionary for
         * the signature
         * information and will insert the /Filter and /SubFilter values into this
         * dictionary.
         * It will leave just a blank placeholder for the signature that is to be
         * inserted later.
         */
        IExternalSignatureContainer external = new ExternalBlankSignatureContainer(PdfName.Adobe_PPKLite,
                PdfName.Adbe_pkcs7_detached);

        // Sign the document using an external container.
        // 8192 is the size of the empty signature placeholder.
        signer.signExternalContainer(external, 8192);
    }

    public void createSignature(String src, String dest, String fieldName, PrivateKey pk, Certificate[] chain)
            throws IOException, GeneralSecurityException {
        PdfReader reader = new PdfReader(src);
        try (FileOutputStream os = new FileOutputStream(dest)) {
            PdfSigner signer = new PdfSigner(reader, os, new StampingProperties());

            IExternalSignatureContainer external = new MyExternalSignatureContainer(pk, chain);

            // Signs a PDF where space was already reserved. The field must cover the whole
            // document.
            signer.signDeferred(signer.getDocument(), fieldName, os, external);
        }
    }

    class MyExternalSignatureContainer implements IExternalSignatureContainer {

        protected PrivateKey pk;
        protected Certificate[] chain;

        public MyExternalSignatureContainer(PrivateKey pk, Certificate[] chain) {
            this.pk = pk;
            this.chain = chain;
        }

        @Override
        public byte[] sign(InputStream is) throws GeneralSecurityException {
            try {
                PrivateKeySignature signature = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, "BC");
                String digestAlgorithm = signature.getHashAlgorithm();
                BouncyCastleDigest digest = new BouncyCastleDigest();

                PdfPKCS7 sgn = new PdfPKCS7(null, chain, digestAlgorithm, null, digest, false);
                byte hash[] = DigestAlgorithms.digest(is, digest.getMessageDigest(DigestAlgorithms.SHA256));
                byte[] sh = sgn.getAuthenticatedAttributeBytes(hash, PdfSigner.CryptoStandard.CMS, null, null);
                byte[] extSignature = signature.sign(sh);
                sgn.setExternalDigest(extSignature, null, signature.getEncryptionAlgorithm());

                return sgn.getEncodedPKCS7(hash, PdfSigner.CryptoStandard.CMS, null, null, null);
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }

        @Override
        public void modifySigningDictionary(PdfDictionary arg0) {
            // TODO Auto-generated method stub

        }

    }
}
