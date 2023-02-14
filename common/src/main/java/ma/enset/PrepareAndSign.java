package ma.enset;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfSignatureFormField;
import com.itextpdf.io.codec.brotli.dec.Dictionary;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Scanner;

public class PrepareAndSign {
        public static final String KEYSTORE = "src/main/ressources/mykeystore.p12";
        public static final String PASSWORD = "mypassword";
        public static final String SRC = "src/main/ressources/Hello.pdf";
        public static final String DEST = "src/main/ressources/res/";
        public static final String temp = "src/main/ressources/tmp/tmp.pdf";

        public static final String SIGNAME = "Signature1";

        public static final String[] RESULT_FILES = new String[] {
                        "hello_empty.pdf",
                        "hello_empty2.pdf",
                        "field_signed.pdf",
                        "apparence_signed1.pdf",
                        "font_signed2.pdf",
                        "image_signed3.pdf",
                        "app_signed4.pdf",
        };

        public void createPdf(String filename) throws IOException {
                PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
                Document doc = new Document(pdfDoc);

                doc.add(new Paragraph("Hello World!"));
                PdfWidgetAnnotation widget = new PdfWidgetAnnotation(new Rectangle(72, 632, 200, 100));
                PdfFormField field = PdfFormField.createSignature(pdfDoc);
                field.addKid(widget);
                field.setPage(1);
                field.setFieldName(SIGNAME);
                // field.get
                /*
                 * PdfFormField field = new SignatureFormFieldBuilder(pdfDoc, SIGNAME)
                 * .setWidgetRectangle(new Rectangle(72, 632, 200, 100)).createSignature();
                 * field.getFirstFormAnnotation().setPage(1);
                 */

                // Set the widget properties
                field.getWidgets().get(0).setHighlightMode(PdfAnnotation.HIGHLIGHT_INVERT)
                                .setFlags(PdfAnnotation.PRINT);

                PdfDictionary mkDictionary = field.getWidgets().get(0).getAppearanceCharacteristics();
                if (null == mkDictionary) {
                        mkDictionary = new PdfDictionary();
                }

                PdfArray black = new PdfArray();
                black.add(new PdfNumber(ColorConstants.BLACK.getColorValue()[0]));
                black.add(new PdfNumber(ColorConstants.BLACK.getColorValue()[1]));
                black.add(new PdfNumber(ColorConstants.BLACK.getColorValue()[2]));
                mkDictionary.put(PdfName.BC, black);

                PdfArray white = new PdfArray();
                white.add(new PdfNumber(ColorConstants.WHITE.getColorValue()[0]));
                white.add(new PdfNumber(ColorConstants.WHITE.getColorValue()[1]));
                white.add(new PdfNumber(ColorConstants.WHITE.getColorValue()[2]));
                mkDictionary.put(PdfName.BG, white);

                field.getWidgets().get(0).setAppearanceCharacteristics(mkDictionary);

                PdfAcroForm.getAcroForm(pdfDoc, true).addField(field);

                Rectangle rect = new Rectangle(0, 50, 200, 100);
                PdfFormXObject xObject = new PdfFormXObject(rect);
                PdfCanvas canvas = new PdfCanvas(xObject, pdfDoc);
                canvas
                                .setStrokeColor(ColorConstants.BLUE)
                                .setFillColor(ColorConstants.LIGHT_GRAY)
                                .rectangle(0 + 0.5, 0 + 0.5, 200 - 0.5, 100 - 0.5)
                                .fillStroke()
                                .setFillColor(ColorConstants.BLUE);
                new Canvas(canvas, rect).showTextAligned("SIGN HERE", 100, 50,
                                TextAlignment.CENTER, (float) Math.toRadians(25));

                // Note that Acrobat doesn't show normal appearance in the highlight mode.
                field.getWidgets().get(0).setNormalAppearance(xObject.getPdfObject());

                doc.close();
        }

        public void addField(String src, String dest) throws Exception {
                PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));
                PdfWidgetAnnotation widget = new PdfWidgetAnnotation(new Rectangle(290, 200, 300, 60));
                PdfFormField field = PdfFormField.createSignature(pdfDoc);
                field.addKid(widget);
                field.setPage(1);
                field.setFieldName(SIGNAME);
                // Create a signature form field
                /*
                 * PdfSignatureFormField field = new SignatureFormFieldBuilder(pdfDoc, SIGNAME)
                 * .setWidgetRectangle(new Rectangle(72, 632, 200, 100)).createSignature();
                 */

                field.getWidgets().get(0).setHighlightMode(PdfAnnotation.HIGHLIGHT_OUTLINE)
                                .setFlags(PdfAnnotation.PRINT);

                PdfAcroForm.getAcroForm(pdfDoc, true).addField(field);

                pdfDoc.close();
        }

        public void sign(String src, String name, String dest, Certificate[] chain, PrivateKey pk,
                        String digestAlgorithm,
                        String provider, PdfSigner.CryptoStandard subfilter, String reason, String location)
                        throws GeneralSecurityException, IOException {
                PdfReader reader = new PdfReader(src);
                PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), new StampingProperties());

                // Create the signature appearance
                signer.getSignatureAppearance()
                                .setReason(reason)
                                .setLocation(location);

                signer.setFieldName(name);

                IExternalSignature pks = new PrivateKeySignature(pk, digestAlgorithm, provider);
                IExternalDigest digest = new BouncyCastleDigest();

                // Sign the document using the detached mode, CMS or CAdES equivalent.
                signer.signDetached(digest, pks, chain, null, null, null, 0, subfilter);
        }

        public static void main(String[] args) throws Exception {
                File file = new File(DEST);
                file.mkdirs();
                int signMethod = 0;
                PrepareAndSign app = new PrepareAndSign();
                // app.createPdf(DEST + RESULT_FILES[0]);
                app.addField(SRC, DEST + RESULT_FILES[1]);

                // Provider
                BouncyCastleProvider provider = new BouncyCastleProvider();
                Security.addProvider(provider);

                Scanner scanner = new Scanner(System.in);
                System.out.println("Enter the signature method");
                signMethod = scanner.nextInt();

                SignMethods signMethods = new SignMethods();

                switch (signMethod) {
                        case 1:
                                signMethods.signFirst(DEST + RESULT_FILES[1], SIGNAME, DEST + RESULT_FILES[2],
                                                DigestAlgorithms.SHA256, provider.getName(),
                                                PdfSigner.CryptoStandard.CMS,
                                                "Test", "Ghent");
                                break;
                        case 2:
                                signMethods.signApparence1(DEST + RESULT_FILES[1], SIGNAME, DEST + RESULT_FILES[3],
                                                DigestAlgorithms.SHA256, provider.getName(),
                                                PdfSigner.CryptoStandard.CMS,
                                                "Test", "Ghent");
                                break;
                        case 3:
                                signMethods.signWithFont(DEST + RESULT_FILES[1], SIGNAME, DEST + RESULT_FILES[4],
                                                DigestAlgorithms.SHA256, provider.getName(),
                                                PdfSigner.CryptoStandard.CMS,
                                                "Test", "Ghent");
                                break;
                        case 4:
                                signMethods.signBackImage(DEST + RESULT_FILES[1], SIGNAME, DEST + RESULT_FILES[5],
                                                DigestAlgorithms.SHA256, provider.getName(),
                                                PdfSigner.CryptoStandard.CMS,
                                                "Test", "Morocco");
                                break;
                        case 5:
                                signMethods.signWithImageGraphic(DEST + RESULT_FILES[1], SIGNAME, DEST + RESULT_FILES[6],
                                                DigestAlgorithms.SHA256, provider.getName(),
                                                PdfSigner.CryptoStandard.CMS,
                                                "Test", "MA",PdfSignatureAppearance.RenderingMode.GRAPHIC_AND_DESCRIPTION);
                                break;
                        default:
                                System.out.println("Invalid option");
                }
                
        }
}
