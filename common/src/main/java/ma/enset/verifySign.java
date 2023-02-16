package ma.enset;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CRL;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.signatures.CRLVerifier;
import com.itextpdf.signatures.CertificateInfo;
import com.itextpdf.signatures.CertificateVerification;
import com.itextpdf.signatures.OCSPVerifier;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.SignaturePermissions;
import com.itextpdf.signatures.SignatureUtil;
import com.itextpdf.signatures.TimestampConstants;
import com.itextpdf.signatures.VerificationException;
import com.itextpdf.signatures.VerificationOK;

import java.io.IOException;
import java.io.PrintStream;

public class verifySign {
    public static final String SRC = "src/main/ressources/res/hello_signed1.pdf";

    public void ListSignatures(String path) throws IOException, GeneralSecurityException, java.io.IOException {
        System.out.println(path);
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(path));
        SignatureUtil signUtil = new SignatureUtil(pdfDoc);
        List<String> names = signUtil.getSignatureNames();

        System.out.println(path);
        for (String name : names) {
            System.out.println("===== " + name + " =====");
            // verifySignature(signUtil, name);
        }
        System.out.println();
        pdfDoc.close();
    }

    public PdfPKCS7 checkSignatureIntegrety(SignatureUtil signUtil, String name) throws GeneralSecurityException {
        PdfPKCS7 pkcs7 = signUtil.readSignatureData(name);
        System.out.println("Signature covers whole document: " + signUtil.signatureCoversWholeDocument(name));
        System.out.println("Document revision: " + signUtil.getRevision(name) + " of " + signUtil.getTotalRevisions());
        System.out.println("Integrity check OK? " + pkcs7.verifySignatureIntegrityAndAuthenticity());
        return pkcs7;
    }

    public void verifySignatures(String path) throws IOException, GeneralSecurityException, java.io.IOException {
        System.out.println(path);
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(path));
        SignatureUtil signUtil = new SignatureUtil(pdfDoc);
        List<String> names = signUtil.getSignatureNames();

        System.out.println(path);
        for (String name : names) {
            System.out.println("===== " + name + " =====");
            checkSignatureIntegrety(signUtil, name);
        }
        System.out.println();
        pdfDoc.close();
    }

    public SignaturePermissions inspectSignature(PdfDocument pdfDoc, SignatureUtil signUtil, PdfAcroForm form,
            String name, SignaturePermissions perms) throws GeneralSecurityException {
        List<PdfWidgetAnnotation> widgets = form.getField(name).getWidgets();

        // Check the visibility of the signature annotation
        if (widgets != null && widgets.size() > 0) {
            Rectangle pos = widgets.get(0).getRectangle().toRectangle();
            int pageNum = pdfDoc.getPageNumber(widgets.get(0).getPage());
            if (pos.getWidth() == 0 || pos.getHeight() == 0) {
                System.out.println("Invisible signature");
            } else {
                System.out.println(String.format("Field on page %s; llx: %s, lly: %s, urx: %s; ury: %s",
                        pageNum, pos.getLeft(), pos.getBottom(), pos.getRight(), pos.getTop()));
            }
        }

        /*
         * Find out how the message digest of the PDF bytes was created,
         * how these bytes and additional attributes were signed
         * and how the signed bytes are stored in the PDF
         */
        PdfPKCS7 pkcs7 = checkSignatureIntegrety(signUtil, name);
        System.out.println("Digest algorithm: " + pkcs7.getHashAlgorithm());
        System.out.println("Encryption algorithm: " + pkcs7.getEncryptionAlgorithm());
        System.out.println("Filter subtype: " + pkcs7.getFilterSubtype());

        // Get the signing certificate to find out the name of the signer.
        X509Certificate cert = (X509Certificate) pkcs7.getSigningCertificate();
        System.out.println("Name of the signer: " + CertificateInfo.getSubjectFields(cert).getField("CN"));
        if (pkcs7.getSignName() != null) {
            System.out.println("Alternative name of the signer: " + pkcs7.getSignName());
        }

        // Get the signing time
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");

        /*
         * Mind that the getSignDate() method is not that secure as timestamp
         * because it's based only on signature author claim. I.e. this value can only
         * be trusted
         * if signature is trusted and it cannot be used for signature verification.
         */
        System.out.println("Signed on: " + date_format.format(pkcs7.getSignDate().getTime()));

        /*
         * If a timestamp was applied, retrieve information about it.
         * Timestamp is a secure source of signature creation time,
         * because it's based on Time Stamping Authority service.
         */
        if (TimestampConstants.UNDEFINED_TIMESTAMP_DATE != pkcs7.getTimeStampDate()) {
            System.out.println("TimeStamp: " + date_format.format(pkcs7.getTimeStampDate().getTime()));
            // TSTInfo ts = pkcs7.getTimeStampToken();
            System.out.println("TimeStamp service: " + pkcs7.getTimeStampToken().getTimeStampInfo());
            System.out.println("Timestamp verified? " + pkcs7.verifyTimestampImprint());
        }

        System.out.println("Location: " + pkcs7.getLocation());
        System.out.println("Reason: " + pkcs7.getReason());

        /*
         * If you want less common entries than PdfPKCS7 object has, such as the contact
         * info,
         * you should use the signature dictionary and get the properties by name.
         */
        PdfDictionary sigDict = signUtil.getSignatureDictionary(name);
        PdfString contact = sigDict.getAsString(PdfName.ContactInfo);
        if (contact != null) {
            System.out.println("Contact info: " + contact);
        }

        /*
         * Every new signature can add more restrictions to a document, but it can't
         * take away previous restrictions.
         * So if you want to retrieve information about signatures restrictions, you
         * need to pass
         * the SignaturePermissions instance of the previous signature, or null if there
         * was none.
         */
        perms = new SignaturePermissions(sigDict, perms);
        System.out.println("Signature type: " + (perms.isCertification() ? "certification" : "approval"));
        System.out.println("Filling out fields allowed: " + perms.isFillInAllowed());
        System.out.println("Adding annotations allowed: " + perms.isAnnotationsAllowed());
        for (SignaturePermissions.FieldLock lock : perms.getFieldLocks()) {
            System.out.println("Lock: " + lock.toString());
        }

        return perms;
    }

    public void inspectSignatures(String path) throws IOException, GeneralSecurityException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(path));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, false);
        SignaturePermissions perms = null;
        SignatureUtil signUtil = new SignatureUtil(pdfDoc);
        List<String> names = signUtil.getSignatureNames();

        System.out.println(path);
        for (String name : names) {
            System.out.println("===== " + name + " =====");
            perms = inspectSignature(pdfDoc, signUtil, form, name, perms);
        }
    }

    public static void main(String[] args) throws IOException, GeneralSecurityException, java.io.IOException {
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);

        verifySign app = new verifySign();
        app.ListSignatures(SRC);
      //  app.verifySignatures(SRC);
         app.inspectSignatures(SRC);
    }


    


    private static PrintStream OUT_STREAM = System.out;
    private KeyStore ks;

  

    public PdfPKCS7 verifySignature(SignatureUtil signUtil, String name) throws GeneralSecurityException,
            IOException {
        PdfPKCS7 pkcs7 = getSignatureData(signUtil, name);
        Certificate[] certs = pkcs7.getSignCertificateChain();

        // Timestamp is a secure source of signature creation time,
        // because it's based on Time Stamping Authority service.
        Calendar cal = pkcs7.getTimeStampDate();

        // If there is no timestamp, use the current date
        if (TimestampConstants.UNDEFINED_TIMESTAMP_DATE == cal) {
            cal = Calendar.getInstance();
        }

        // Check if the certificate chain, presented in the PDF, can be verified against
        // the created key store.
        List<VerificationException> errors = CertificateVerification.verifyCertificates(certs, ks, cal);
        if (errors.size() == 0) {
            OUT_STREAM.println("Certificates verified against the KeyStore");
        } else {
            OUT_STREAM.println(errors);
        }

        // Find out if certificates were valid on the signing date, and if they are still valid today
        for (int i = 0; i < certs.length; i++) {
            X509Certificate cert = (X509Certificate) certs[i];
            OUT_STREAM.println("=== Certificate " + i + " ===");
            showCertificateInfo(cert, cal.getTime());
        }

        // Take the signing certificate
        X509Certificate signCert = (X509Certificate) certs[0];

        // Take the certificate of the issuer of that certificate (or null if it was self-signed).
        X509Certificate issuerCert = (certs.length > 1 ? (X509Certificate) certs[1] : null);

        OUT_STREAM.println("=== Checking validity of the document at the time of signing ===");
        checkRevocation(pkcs7, signCert, issuerCert, cal.getTime());

        OUT_STREAM.println("=== Checking validity of the document today ===");
        checkRevocation(pkcs7, signCert, issuerCert, new Date());

        return pkcs7;
    }

    public PdfPKCS7 getSignatureData(SignatureUtil signUtil, String name) throws GeneralSecurityException {
        PdfPKCS7 pkcs7 = signUtil.readSignatureData(name);

        OUT_STREAM.println("Signature covers whole document: " + signUtil.signatureCoversWholeDocument(name));
        OUT_STREAM.println("Document revision: " + signUtil.getRevision(name) + " of " + signUtil.getTotalRevisions());
        OUT_STREAM.println("Integrity check OK? " + pkcs7.verifySignatureIntegrityAndAuthenticity());

        return pkcs7;
    }

    public void showCertificateInfo(X509Certificate cert, Date signDate) {
        OUT_STREAM.println("Issuer: " + cert.getIssuerDN());
        OUT_STREAM.println("Subject: " + cert.getSubjectDN());
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        date_format.setTimeZone(TimeZone.getTimeZone("Universal"));
        OUT_STREAM.println("Valid from: " + date_format.format(cert.getNotBefore()));
        OUT_STREAM.println("Valid to: " + date_format.format(cert.getNotAfter()));

        // Check if a certificate was valid on the signing date
        try {
            cert.checkValidity(signDate);
            OUT_STREAM.println("The certificate was valid at the time of signing.");
        } catch (CertificateExpiredException e) {
            OUT_STREAM.println("The certificate was expired at the time of signing.");
        } catch (CertificateNotYetValidException e) {
            OUT_STREAM.println("The certificate wasn't valid yet at the time of signing.");
        }

        // Check if a certificate is still valid now
        try {
            cert.checkValidity();
            OUT_STREAM.println("The certificate is still valid.");
        } catch (CertificateExpiredException e) {
            OUT_STREAM.println("The certificate has expired.");
        } catch (CertificateNotYetValidException e) {
            OUT_STREAM.println("The certificate isn't valid yet.");
        }
    }

    public static void checkRevocation(PdfPKCS7 pkcs7, X509Certificate signCert, X509Certificate issuerCert, Date date)
            throws GeneralSecurityException, IOException {
        List<BasicOCSPResp> ocsps = new ArrayList<>();
        if (pkcs7.getOcsp() != null) {
            ocsps.add(pkcs7.getOcsp());
        }

        // Check if the OCSP responses in the list were valid for the certificate on a specific date.
        OCSPVerifier ocspVerifier = new OCSPVerifier(null, ocsps);
        List<VerificationOK> verification = ocspVerifier.verify(signCert, issuerCert, date);

        // If that list is empty, we can't verify using OCSP, and we need to look for CRLs.
        if (verification.size() == 0) {
            List<X509CRL> crls = new ArrayList<X509CRL>();
            if (pkcs7.getCRLs() != null) {
                for (CRL crl : pkcs7.getCRLs()) {
                    crls.add((X509CRL) crl);
                }
            }

            // Check if the CRLs in the list were valid on a specific date.
            CRLVerifier crlVerifier = new CRLVerifier(null, crls);
            verification.addAll(crlVerifier.verify(signCert, issuerCert, date));
        }

        if (verification.size() == 0) {
            OUT_STREAM.println("The signing certificate couldn't be verified");
        } else {
            for (VerificationOK v : verification) {
                OUT_STREAM.println(v);
            }
        }
    }


}
