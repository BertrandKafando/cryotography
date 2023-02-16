package ma.enset;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.CrlClientOnline;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.IExternalDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.IOcspClient;
import com.itextpdf.signatures.ITSAClient;
import com.itextpdf.signatures.OcspClientBouncyCastle;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.TSAClientBouncyCastle;
import com.itextpdf.signatures.PdfSigner.CryptoStandard;

public class BestPracticeSign {
    public static final String KEYSTORE = "src/main/ressources/keyStore.p12";
    public static final String PASSWORD = "mypassword";
    public static final String SRC = "src/main/ressources/Hello.pdf";
    public static final String DEST = "src/main/ressources/res/hello_signed%s.pdf";
    public static final String DEST2 = "src/main/ressources/res/hello_signed.pdf";
    public static final String temp = "src/main/ressources/tmp/tmp.pdf";
    public static final String IMG = "src/main/ressources/imgs/es.png";

    void sign(String src, String dest,
            Certificate[] chain, PrivateKey pk, String digestAlgorithm, String provider,
            CryptoStandard subfilter, String reason, String location)
            throws GeneralSecurityException, java.io.IOException {
        PdfReader reader = new PdfReader(src);
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), temp,
                new StampingProperties().useAppendMode());

        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance.setReason(reason);
        appearance.setLocation(location);
        appearance.setPageRect(new Rectangle(400, 200, 200, 100));
        appearance.setLayer2Text("This document was signed by Berto Specimen");
        signer.setFieldName("sig");
        signer.setCertificationLevel(PdfSigner.NOT_CERTIFIED);

        IExternalSignature pks = new PrivateKeySignature(pk, digestAlgorithm, provider);
        IExternalDigest digest = new BouncyCastleDigest();

        // Sign the document using the detached mode, CMS or CAdES equivalent.
        signer.signDetached(digest, pks, chain, null, null, null, 0, subfilter);

    }

    public void signBestPractice(String src, String dest, Certificate[] chain, PrivateKey pk,
            String digestAlgorithm, String provider, CryptoStandard subfilter,
            String reason, String location, Collection<ICrlClient> crlList,
            IOcspClient ocspClient, ITSAClient tsaClient, int estimatedSize)
            throws GeneralSecurityException, IOException {
        PdfReader reader = new PdfReader(src);
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), new StampingProperties());

        // Create the signature appearance
        Rectangle rect = new Rectangle(100, 508, 450, 70);
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance
                .setReason(reason)
                .setLocation(location)

                // Specify if the appearance before field is signed will be used
                // as a background for the signed field. The "false" value is the default value.
                .setReuseAppearance(false)
                .setPageRect(rect)
                .setPageNumber(1)
                .setContact("898684585")
                .setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC_AND_DESCRIPTION)
                .setSignatureGraphic(ImageDataFactory.create(IMG));

        signer.setFieldName("sig");

        // Creating the signature
        IExternalSignature pks = new PrivateKeySignature(pk, digestAlgorithm, provider);
        IExternalDigest digest = new BouncyCastleDigest();

        // Sign the document using the detached mode, CMS or CAdES equivalent.
        signer.signDetached(digest, pks, chain, crlList, ocspClient, tsaClient, estimatedSize, subfilter);
    }

    public static void main(String[] args) throws Exception {
        LoadKeystore loadKeystore = new LoadKeystore(KEYSTORE, PASSWORD);
        X509Certificate cert = loadKeystore.getCertificate("mycert");
        PrivateKey privateKey = (PrivateKey) loadKeystore.getPrivateKey("myalias", "mypassword");
        PublicKey publicKey = (PublicKey) loadKeystore.getPublicKey("mycert");
        Certificate[] chain = new Certificate[] { cert };
        // Provider
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        /*
         * Get the CRL list from the CRL distribution point in the certificate
         * we can also use the CRL list from the CA certificate or ofline CRL list
         * 
         * better options iText performs OCSP and CRL checks by default
         * List<CrlClient> crlList = new ArrayList<CrlClient>();
         * crlList.add(new CrlClientOnline(chain))
         */
        ICrlClient crlClient = new CrlClientOnline("https://crl.cacert.org/revoke.crl");
        Collection<ICrlClient> crlList = new ArrayList<ICrlClient>();
        crlList.add(crlClient);

        /*
         * Create an instance of OcspClientBouncyCastle, an implementation of
         * OcspClient.
         * In the current sample it is not needed to verify the OCSP response,
         * that is why null is passed as verifier parameter.
         */
        IOcspClient ocspClient = new OcspClientBouncyCastle(null);

        /*
         * 
         * 
         */
        ITSAClient tsaClient = null;
        for (int i = 0; i < chain.length; i++) {
            X509Certificate certt = (X509Certificate) chain[i];
            String tsaUrl = CertificateUtil.getTSAURL(certt);
            if (tsaUrl != null) {
                tsaClient = new TSAClientBouncyCastle(tsaUrl);
                break;
            }
        }

        // sign simple
        BestPracticeSign app = new BestPracticeSign();
        app.sign(SRC, String.format(DEST, 1), chain, privateKey, DigestAlgorithms.SHA256,
                provider.getName(), CryptoStandard.CMS, "Test 1", "Ghent");

        // sign best practice
        app.signBestPractice(SRC, String.format(DEST, 2), chain, privateKey, DigestAlgorithms.SHA256,
                provider.getName(), CryptoStandard.CMS, "Test 2", "Morocco", crlList, ocspClient, tsaClient, 0);

        /*
         * app.sign(SRC, String.format(DEST, 2), chain, privateKey,
         * DigestAlgorithms.SHA512,
         * provider.getName(), CryptoStandard.CMS, "Test 2", "Ghent");
         * app.sign(SRC, String.format(DEST, 3), chain, privateKey,
         * DigestAlgorithms.SHA256,
         * provider.getName(), CryptoStandard.CADES, "Test 3", "Ghent");
         * app.sign(SRC, String.format(DEST, 4), chain, privateKey,
         * DigestAlgorithms.RIPEMD160,
         * provider.getName(), CryptoStandard.CADES, "Test 4", "Ghent");
         */
    }
}