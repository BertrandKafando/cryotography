package ma.enset;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.swing.text.StyleConstants.FontConstants;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.BaseDirection;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.IExternalDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;

public class SignMethods {
    public static final String KEYSTORE = "src/main/ressources/mykeystore.p12";
    public static final String PASSWORD = "mypassword";
    public static final String temp = "src/main/ressources/tmp/tmp.pdf";
    public static final String SRC = "src/main/ressources/Hello.pdf";
    public static final String DEST = "src/main/ressources/res/hello_signed%s.pdf";
    public static final String IMG = "src/main/ressources/imgs/es.png";
    KeyStore ksKeyStore;
    PrivateKey pkPrivateKey;
    PublicKey pbPublicKey;
    Certificate[] chain;
    X509Certificate cert;

    public SignMethods() throws Exception {
        this.init();
    }

    private void init() throws Exception {
        LoadKeystore loadKeystore = new LoadKeystore(KEYSTORE, PASSWORD);
        cert = loadKeystore.getCertificate("myalias");
        pkPrivateKey = (PrivateKey) loadKeystore.getPrivateKey("myalias", "mypassword");
        pbPublicKey = (PublicKey) loadKeystore.getPublicKey("myalias");
        chain = new Certificate[] { cert };

        // Provider
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
    }

    public void signFirst(String src, String name, String dest, String digestAlgorithm,
            String provider, PdfSigner.CryptoStandard subfilter, String reason, String location)
            throws GeneralSecurityException, IOException {
        PdfReader reader = new PdfReader(src);
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), new StampingProperties());

        // Create the signature appearance
        signer.getSignatureAppearance()
                .setReason(reason)
                .setLocation(location);

        signer.setFieldName(name);

        IExternalSignature pks = new PrivateKeySignature(pkPrivateKey, digestAlgorithm, provider);
        IExternalDigest digest = new BouncyCastleDigest();

        // Sign the document using the detached mode, CMS or CAdES equivalent.
        signer.signDetached(digest, pks, chain, null, null, null, 0, subfilter);
    }

    // add appearance
    public void signApparence1(String src, String name, String dest, String digestAlgorithm, String provider,
            PdfSigner.CryptoStandard subfilter, String reason, String location)
            throws GeneralSecurityException, IOException {
        PdfReader reader = new PdfReader(src);
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), new StampingProperties());

        // Create the signature appearance
        PdfSignatureAppearance appearance = signer.getSignatureAppearance()
                .setReason(reason)
                .setLocation(location);

        // This name corresponds to the name of the field that already exists in the
        // document.
        signer.setFieldName(name);

        // Get the background layer and draw a gray rectangle as a background.
        PdfFormXObject n0 = appearance.getLayer0();
        float x = n0.getBBox().toRectangle().getLeft();
        float y = n0.getBBox().toRectangle().getBottom();
        float width = n0.getBBox().toRectangle().getWidth();
        float height = n0.getBBox().toRectangle().getHeight();
        PdfCanvas canvas = new PdfCanvas(n0, signer.getDocument());
        canvas.setFillColor(ColorConstants.BLUE);
        canvas.rectangle(x, y, width, height);
        canvas.fill();

        // Set the signature information on layer 2
        PdfFormXObject n2 = appearance.getLayer2();
        Paragraph p = new Paragraph("This document was signed by Berto Specimen.");
        new Canvas(n2, signer.getDocument()).add(p);

        signer.setFieldName(name);

        IExternalSignature pks = new PrivateKeySignature(pkPrivateKey, digestAlgorithm, provider);
        IExternalDigest digest = new BouncyCastleDigest();

        // Sign the document using the detached mode, CMS or CAdES equivalent.
        signer.signDetached(digest, pks, chain, null, null, null, 0, subfilter);
    }

    public void signWithFont(String src, String name, String dest, String digestAlgorithm,
            String provider, PdfSigner.CryptoStandard subfilter, String reason, String location)
            throws GeneralSecurityException, IOException {
        PdfReader reader = new PdfReader(src);
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), new StampingProperties());

        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance.setReason(reason);
        appearance.setLocation(location);
        signer.setFieldName(name);

        // Creating the appearance for layer 2
        PdfFormXObject n2 = appearance.getLayer2();

        // Set custom text, custom font, and right-to-left writing.
        // Characters: لورانس العرب
        Text text = new Text("\u0644\u0648\u0631\u0627\u0646\u0633 \u0627\u0644\u0639\u0631\u0628");
        text.setFont(PdfFontFactory.createFont("./src/main/ressources/fonts/NotoNaskhArabic-Regular.ttf",
                PdfEncodings.IDENTITY_H, EmbeddingStrategy.PREFER_EMBEDDED));
        text.setBaseDirection(BaseDirection.RIGHT_TO_LEFT);
        new Canvas(n2, signer.getDocument()).add(new Paragraph(text).setTextAlignment(TextAlignment.RIGHT));

        PrivateKeySignature pks = new PrivateKeySignature(pkPrivateKey, digestAlgorithm, provider);
        IExternalDigest digest = new BouncyCastleDigest();
        signer.signDetached(digest, pks, chain, null, null, null, 0, subfilter);
    }

    public void signBackImage(String src, String name, String dest, String digestAlgorithm,
            String provider, PdfSigner.CryptoStandard subfilter, String reason, String location)
            throws GeneralSecurityException, IOException {
        PdfReader reader = new PdfReader(src);
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), new StampingProperties());

        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance.setReason(reason);
        appearance.setLocation(location);
        appearance.setContact("898684585");
        signer.setFieldName(name);
        appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC_AND_DESCRIPTION);
        appearance.setSignatureGraphic(ImageDataFactory.create(IMG));
        // Set a custom text and a background image
        //appearance.setLayer2Text("This document was signed by Bruno Specimen");
       // appearance.setImage(ImageDataFactory.create(IMG));
       // appearance.setImageScale(1);

        PrivateKeySignature pks = new PrivateKeySignature(pkPrivateKey, digestAlgorithm, provider);
        IExternalDigest digest = new BouncyCastleDigest();
        signer.signDetached(digest, pks, chain, null, null, null, 0, subfilter);
    }

    public void signWithImageAndText(String src, String name, String dest, String digestAlgorithm,
            String provider, PdfSigner.CryptoStandard subfilter, String reason, String location)
            throws GeneralSecurityException, IOException {
        PdfReader reader = new PdfReader(src);
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), new StampingProperties());

        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance.setReason(reason);
        appearance.setLocation(location);
        signer.setFieldName(name);

        // Set a custom text and a scaled background image
        appearance.setLayer2Text("signed by Berto Specimen");
        appearance.setImage(ImageDataFactory.create(IMG));
        appearance.setImageScale(-1);

        PrivateKeySignature pks = new PrivateKeySignature(pkPrivateKey, digestAlgorithm, provider);
        IExternalDigest digest = new BouncyCastleDigest();
        signer.signDetached(digest, pks, chain, null, null, null, 0, subfilter);
    }

    public void signWithImageGraphic(String src, String name, String dest,  String digestAlgorithm,
            String provider, PdfSigner.CryptoStandard subfilter, String reason, String location,
            PdfSignatureAppearance.RenderingMode renderingMode)
            throws GeneralSecurityException, IOException {
        PdfReader reader = new PdfReader(src);
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), new StampingProperties());
        ImageData  image = ImageDataFactory.create(IMG);
        // Create the signature appearance
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance.setReason(reason);
        appearance.setLocation(location);
        appearance.setContact("898684585");

        // This name corresponds to the name of the field that already exists in the
        // document.
        signer.setFieldName(name);

        appearance.setLayer2Text("Signed on " + new Date().toString());

        // Set the rendering mode for this signature.
        appearance.setRenderingMode(renderingMode);

        // Set the Image object to render when the rendering mode is set to
        // RenderingMode.GRAPHIC
        // or RenderingMode.GRAPHIC_AND_DESCRIPTION.
        appearance.setSignatureGraphic(image);

        PrivateKeySignature pks = new PrivateKeySignature(pkPrivateKey, digestAlgorithm, provider);
        IExternalDigest digest = new BouncyCastleDigest();

        // Sign the document using the detached mode, CMS or CAdES equivalent.
        signer.signDetached(digest, pks, chain, null, null, null, 0, subfilter);
    }

}
