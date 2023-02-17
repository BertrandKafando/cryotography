package es.kf.clientside;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDate;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Calendar;

public class Presign extends HttpServlet {
    public static final String SRC = "src/main/resources/Hello.pdf";
    public  void  init() {
        System.out.println("Hello client side");
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/octet-stream");
        try {
            // We get the self-signed certificate from the client
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            Certificate[] chain = new Certificate[1];
            chain[0] = factory.generateCertificate(req.getInputStream());

            // we create a reader and a stamper
            PdfReader reader = new PdfReader(SRC);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfSigner signer = new PdfSigner(reader, baos, new StampingProperties().useAppendMode());

            PdfSignatureAppearance appearance = signer.getSignatureAppearance();
            appearance.setReason("test");
            appearance.setLocation("on a server!");
            appearance.setPageRect(new Rectangle(400, 200, 200, 100));
            appearance.setLayer2Text("This document was signed by Berto ");
            signer.setFieldName("sig");
            signer.setCertificationLevel(PdfSigner.CERTIFIED_NO_CHANGES_ALLOWED);

            PdfSignature dic = new PdfSignature(PdfName.Adobe_PPKLite, PdfName.Adobe_PPKLite);
            dic.setReason(appearance.getReason());
            dic.setLocation(appearance.getLocation());
            dic.setContact(appearance.getContact());

// We create a custom digest
            BouncyCastleDigest digest = new BouncyCastleDigest();
            InputStream is = new FileInputStream(SRC);
            PdfPKCS7 sgn = new PdfPKCS7(null, chain, DigestAlgorithms.SHA256, null, digest, false);
            byte hash[] = DigestAlgorithms.digest(is, digest.getMessageDigest(DigestAlgorithms.SHA256));
            byte[] sh = sgn.getAuthenticatedAttributeBytes(hash, PdfSigner.CryptoStandard.CMS, null, null);

            Calendar cal = Calendar.getInstance();

            // We store the objects we'll need for post signing in a session
            HttpSession session = req.getSession(true);
            session.setAttribute("sgn", sgn);
            session.setAttribute("hash", hash);
            session.setAttribute("cal", cal);
            session.setAttribute("sap", signer.getSignatureAppearance());
            session.setAttribute("baos", baos);
            // we write the hash that needs to be signed to the HttpResponse output

            OutputStream os = resp.getOutputStream();
            os.write(sh, 0, sh.length);
            os.flush();
            os.close();
        } catch (GeneralSecurityException e) {
            throw new IOException(e);
        }
    }

}
