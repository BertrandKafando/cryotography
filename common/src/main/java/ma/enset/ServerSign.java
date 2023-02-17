package ma.enset;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;

public class ServerSign {
    public static final String SRC = "src/main/ressources/Hello.pdf";
    // public static final String CERT =
    // "https://demo.itextsupport.com/SigningApp/itextpdf.cer";
    public static final String CERT = "src/main/ressources/keyStore.p12";
    public static final String DEST = "src/main/ressources/res/";
    public static final String PASSWORD = "mypassword";
    public static final String[] RESULT_FILES = new String[] {
            "hello_server.pdf"
    };

    public static void main(String[] args) throws Exception {
        /*
         * CertificateFactory factory = CertificateFactory.getInstance("X.509");
         * URL certUrl = new URL(CERT);
         * Certificate[] chain = new Certificate[1];
         * try (InputStream stream = certUrl.openStream()) {
         * chain[0] = factory.generateCertificate(stream);
         * }
         */
        LoadKeystore loadKeystore = new LoadKeystore(CERT, PASSWORD);
        X509Certificate cert = loadKeystore.getCertificate("mycert");
        PrivateKey privateKey = (PrivateKey) loadKeystore.getPrivateKey("myalias", "mypassword");
        PublicKey publicKey = (PublicKey) loadKeystore.getPublicKey("mycert");
        Certificate[] chain = new Certificate[] { cert };

        new ServerSign().sign(SRC, DEST + RESULT_FILES[0], chain, PdfSigner.CryptoStandard.CMS,
                "Test", "Ghent");
    }

    public void sign(String src, String dest, Certificate[] chain, PdfSigner.CryptoStandard subfilter,
            String reason, String location) throws GeneralSecurityException, IOException {
        PdfReader reader = new PdfReader(src);
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), new StampingProperties());

        // Create the signature appearance
        Rectangle rect = new Rectangle(36, 648, 200, 100);
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance
                .setReason(reason)
                .setLocation(location)
                .setPageRect(rect)
                .setPageNumber(1);
        signer.setFieldName("sig");

        IExternalDigest digest = new BouncyCastleDigest();
        IExternalSignature signature = new ServerSignature();

        // Sign the document using the detached mode, CMS or CAdES equivalent.
        signer.signDetached(digest, signature, chain, null, null, null,
                0, subfilter);
    }

    public class ServerSignature implements IExternalSignature {
        public static final String SIGN = "http://localhost:8080/client_side_war_exploded/sign";

        public String getDigestAlgorithmName() {
            return DigestAlgorithms.SHA256;
        }

        public String getSignatureAlgorithmName() {
            return "RSA";
        }

        public byte[] sign(byte[] message) throws GeneralSecurityException {
            try {
                URL url = new URL(SIGN);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.connect();

                OutputStream os = conn.getOutputStream();
                os.write(message);
                os.flush();
                os.close();

                InputStream is = conn.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] b = new byte[32];
                int read;
                while ((read = is.read(b)) != -1) {
                    baos.write(b, 0, read);
                }

                is.close();

                return baos.toByteArray();
            } catch (IOException e) {
                throw new PdfException(e);
            }
        }

        @Override
        public String getEncryptionAlgorithm() {
            return "RSA";

        }

        @Override
        public String getHashAlgorithm() {
            return DigestAlgorithms.SHA256;
        }
    }

}
