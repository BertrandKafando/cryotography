package ma.enset;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.PdfSigFieldLock;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.IExternalDigest;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Scanner;

public class Workflow {
    public static final String KEYSTORE = "src/main/ressources/mykeystore.p12";
    public static final String FORM = "src/main/ressources/Hello2.pdf";
    public static final String DEST = "src/main/ressources/lock/";

    public static final String ALICE = "./src/main/ressources/alice";
    public static final String CAROL = "./src/main/ressources/carol.p12";
    public static final char[] PASSWORD = "mypassword".toCharArray();

    public static final String[] RESULT_FILES = new String[] {
            "step1_signed_by_alice.pdf", "step2_signed_by_alice_and_filled_out_by_bob.pdf",
            "step3_signed_by_alice_and_bob.pdf", "step4_signed_by_alice_and_bob_filled_out_by_carol.pdf",
            "step5_signed_by_alice_bob_and_carol.pdf", "step6_signed_by_alice_bob_carol_and_dave.pdf"
    };
    public static final String[] RESULT_FILES_L = new String[] {
            "step_1_signed_by_alice.pdf", "step_2_signed_by_alice_and_bob.pdf",
            "step_3_signed_by_alice_bob_and_carol.pdf", "step_5_signed_by_alice_and_bob_broken_by_chuck.pdf", "step_6_signed_by_dave_broken_by_chuck.pdf"
    };

    public static void main(String[] args) throws IOException, GeneralSecurityException {

        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);

        Workflow app = new Workflow();

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the number : ");
        int number = sc.nextInt();

        switch (number) {
            case 1:
                app.createForm();
                String aliceCertifiedFile = DEST + RESULT_FILES[0];
                app.certify(ALICE, provider.getName(), FORM, "sig1", aliceCertifiedFile);

                String bobFilledFile = DEST + RESULT_FILES[1];
                String bobSignedFile = DEST + RESULT_FILES[2];
                app.fillOut(aliceCertifiedFile, bobFilledFile,
                        "approved_bob", "Read and Approved by Bob");

                app.sign(KEYSTORE, provider.getName(), bobFilledFile, "sig2",
                        bobSignedFile);

                String carolFilledFile = DEST + RESULT_FILES[3];
                String carolSignedFile = DEST + RESULT_FILES[4];
                app.fillOut(bobSignedFile, carolFilledFile,
                        "approved_carol", "Read and Approved by Carol");
                app.sign(CAROL, provider.getName(), carolFilledFile, "sig3",
                        carolSignedFile);
                break;

            case 2:
                app.createFormWithLock();

                app.certify(ALICE, provider.getName(), FORM, "sig1", DEST + RESULT_FILES_L[0]);

                app.fillOutAndSign(KEYSTORE, provider.getName(), DEST + RESULT_FILES_L[0], "sig2", "approved_bob",
                        "Read and Approved by Bob", DEST + RESULT_FILES_L[1]);

                app.fillOutAndSign(CAROL, provider.getName(), DEST + RESULT_FILES_L[1], "sig3", "approved_carol",
                        "Read and Approved by Carol", DEST + RESULT_FILES_L[2]);

                app.fillOut(DEST + RESULT_FILES_L[1], DEST + RESULT_FILES_L[3],
                        "approved_bob", "Changed by Chuck");

                app.fillOut(DEST + RESULT_FILES_L[2], DEST + RESULT_FILES_L[4],
                        "approved_carol", "Changed by Chuck");
                break;
            default:
                System.out.println("Invalid number");
                break;
        }

    }

    public void createFormWithLock() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(FORM));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        table.addCell("Written by Alice");
        table.addCell(createSignatureFieldCell("sig1", null));

        table.addCell("For approval by Bob");
        table.addCell(createTextFieldCell("approved_bob"));
        PdfSigFieldLock lock = new PdfSigFieldLock()
                .setFieldLock(PdfSigFieldLock.LockAction.EXCLUDE, "sig1", "approved_bob", "sig2");
        table.addCell(createSignatureFieldCell("sig2", lock));

        table.addCell("For approval by Carol");
        table.addCell(createTextFieldCell("approved_carol"));
        lock = new PdfSigFieldLock().setDocumentPermissions(PdfSigFieldLock.LockPermissions.NO_CHANGES_ALLOWED);
        table.addCell(createSignatureFieldCell("sig3", lock));
    

        doc.add(table);

        doc.close();
    }

    public void createForm() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(FORM));
        Document doc = new Document(pdfDoc);

        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        table.addCell("Written by Alice");
        table.addCell(createSignatureFieldCell("sig1"));

        table.addCell("For approval by Bob");
        table.addCell(createTextFieldCell("approved_bob"));
        table.addCell(createSignatureFieldCell("sig2"));

        table.addCell("For approval by Carol");
        table.addCell(createTextFieldCell("approved_carol"));
        table.addCell(createSignatureFieldCell("sig3"));
        doc.add(table);

        doc.close();
    }

    public void certify(String keystore, String provider, String src, String name, String dest)
            throws GeneralSecurityException, IOException {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(new FileInputStream(keystore), PASSWORD);
        String alias = ks.aliases().nextElement();
        PrivateKey pk = (PrivateKey) ks.getKey(alias, PASSWORD);
        Certificate[] chain = ks.getCertificateChain(alias);

        PdfReader reader = new PdfReader(src);
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), new StampingProperties().useAppendMode());

        // Set signer options
        signer.setFieldName(name);
        signer.setCertificationLevel(PdfSigner.CERTIFIED_FORM_FILLING);

        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, provider);
        IExternalDigest digest = new BouncyCastleDigest();

        // Sign the document using the detached mode, CMS or CAdES equivalent.
        signer.signDetached(digest, pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CMS);
    }

    public void fillOut(String src, String dest, String name, String value) throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest),
                new StampingProperties().useAppendMode());

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        form.getField(name).setValue(value);
        form.getField(name).setReadOnly(true);

        pdfDoc.close();
    }

    public void sign(String keystore, String provider, String src, String name, String dest)
            throws GeneralSecurityException, IOException {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(new FileInputStream(keystore), PASSWORD);
        String alias = ks.aliases().nextElement();
        PrivateKey pk = (PrivateKey) ks.getKey(alias, PASSWORD);
        Certificate[] chain = ks.getCertificateChain(alias);

        PdfReader reader = new PdfReader(src);
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), new StampingProperties().useAppendMode());
        signer.setFieldName(name);

        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, provider);
        IExternalDigest digest = new BouncyCastleDigest();
        signer.signDetached(digest, pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CMS);
    }

    public void fillOutAndSign(String keystore, String provider, String src, String name, String fname, String value,
            String dest)
            throws GeneralSecurityException, IOException {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(new FileInputStream(keystore), PASSWORD);
        String alias = ks.aliases().nextElement();
        PrivateKey pk = (PrivateKey) ks.getKey(alias, PASSWORD);
        Certificate[] chain = ks.getCertificateChain(alias);

        PdfReader reader = new PdfReader(src);
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), new StampingProperties().useAppendMode());
        signer.setFieldName(name);

        PdfAcroForm form = PdfAcroForm.getAcroForm(signer.getDocument(), true);
        form.getField(fname).setValue(value);
        form.getField(fname).setReadOnly(true);

        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, provider);
        IExternalDigest digest = new BouncyCastleDigest();
        signer.signDetached(digest, pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CMS);
    }

    protected static Cell createTextFieldCell(String name) {
        Cell cell = new Cell();
        cell.setHeight(20);
        cell.setNextRenderer(new TextFieldCellRenderer(cell, name));
        return cell;
    }

    protected static Cell createSignatureFieldCell(String name) {
        Cell cell = new Cell();
        cell.setHeight(50);
        cell.setNextRenderer(new SignatureFieldCellRenderer(cell, name));
        return cell;
    }

    protected static Cell createSignatureFieldCell(String name, PdfSigFieldLock lock) {
        Cell cell = new Cell();
        cell.setHeight(50);
        cell.setNextRenderer(new SignatureFieldCellRendererWithLock(cell, name, lock));
        return cell;
    }

    private static class TextFieldCellRenderer extends CellRenderer {
        public String name;

        public TextFieldCellRenderer(Cell modelElement, String name) {
            super(modelElement);
            this.name = name;
        }

        @Override
        public void draw(DrawContext drawContext) {
            super.draw(drawContext);
            PdfWidgetAnnotation widget = new PdfWidgetAnnotation(getOccupiedAreaBBox());
            PdfFormField field = PdfFormField.createText(drawContext.getDocument(), getOccupiedAreaBBox(), name, "");
            /*
             * PdfFormField field = new TextFormFieldBuilder(drawContext.getDocument(),
             * name)
             * .setWidgetRectangle(getOccupiedAreaBBox()).createText();
             */

            PdfAcroForm.getAcroForm(drawContext.getDocument(), true).addField(field);
        }
    }

    private static class SignatureFieldCellRendererWithLock extends CellRenderer {
        public String name;
        public PdfSigFieldLock lock;

        public SignatureFieldCellRendererWithLock(Cell modelElement, String name, PdfSigFieldLock lock) {
            super(modelElement);
            this.name = name;
            this.lock = lock;
        }

        @Override
        public void draw(DrawContext drawContext) {
            super.draw(drawContext);
            PdfWidgetAnnotation widget = new PdfWidgetAnnotation(getOccupiedAreaBBox());
            PdfFormField field = PdfFormField.createSignature(drawContext.getDocument());
            field.addKid(widget);
            field.setPage(1);
            field.setFieldName(name);
            if (lock != null) {
                field.put(PdfName.Lock, lock.makeIndirect(drawContext.getDocument()).getPdfObject());
            }

            field.getWidgets().get(0).setFlag(PdfAnnotation.PRINT);
            field.getWidgets().get(0).setHighlightMode(PdfAnnotation.HIGHLIGHT_INVERT);
            PdfAcroForm.getAcroForm(drawContext.getDocument(), true).addField(field);
        }
    }

    private static class SignatureFieldCellRenderer extends CellRenderer {
        public String name;

        public SignatureFieldCellRenderer(Cell modelElement, String name) {
            super(modelElement);
            this.name = name;
        }

        @Override
        public void draw(DrawContext drawContext) {
            super.draw(drawContext);
            PdfWidgetAnnotation widget = new PdfWidgetAnnotation(getOccupiedAreaBBox());
            PdfFormField field = PdfFormField.createSignature(drawContext.getDocument());
            field.addKid(widget);
            field.setPage(1);
            field.setFieldName(name);

            field.getWidgets().get(0).setHighlightMode(PdfAnnotation.HIGHLIGHT_INVERT);
            field.getWidgets().get(0).setFlags(PdfAnnotation.PRINT);
            PdfAcroForm.getAcroForm(drawContext.getDocument(), true).addField(field);
        }
    }
}
