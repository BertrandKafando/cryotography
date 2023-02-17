package ma.enset;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.List;

public class ClientSign1 {

    public static final String SRC = "src/main/ressources/Hello.pdf";
    // public static final String CERT =
    // "https://demo.itextsupport.com/SigningApp/itextpdf.cer";
    public static final String KEYSTORE = "src/main/ressources/keyStore.p12";
    public static final String CERT = "src/main/ressources/berto.crt";
    public static final String DEST = "src/main/ressources/servlet/";

    public static final String[] RESULT_FILES = new String[] {
            "hello_server2.pdf"
    };
    public static final String PASSWORD = "mypassword";

    public static final String PRE = "http://localhost:8080/client_side_war_exploded/presign";

    public static void main(String[] args) throws IOException, GeneralSecurityException {

        // Make a connection to a PreSign servlet to ask to create a document,
        // then calculate its hash and send it to us
        URL url = new URL(PRE);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.connect();

        // Upload your self-signed certificate
        OutputStream os = conn.getOutputStream();
        FileInputStream fis = new FileInputStream(CERT);
        int read;
        byte[] data = new byte[256];
        while ((read = fis.read(data, 0, data.length)) != -1) {
            os.write(data, 0, read);
        }

        os.flush();
        os.close();

        // Use cookies to maintain a session
        List<String> cookies = conn.getHeaderFields().get("Set-Cookie");

        // Receive a hash that needs to be signed
        InputStream is = conn.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        data = new byte[256];
        while ((read = is.read(data)) != -1) {
            baos.write(data, 0, read);
        }

        is.close();
        byte[] hash = baos.toByteArray();

        // Load your private key from the key store
        LoadKeystore loadKeystore = new LoadKeystore(KEYSTORE, PASSWORD);
        PrivateKey pk = (PrivateKey) loadKeystore.getPrivateKey("myalias", "mypassword");
        // Sign the hash received from the server
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(pk);
        sig.update(hash);
        data = sig.sign();

        // Make a connection to the PostSign Servlet to send the signed bytes to the
        // server.
        url = new URL(POST);
        conn = (HttpURLConnection) url.openConnection();
        for (String cookie : cookies) {
            conn.addRequestProperty("Cookie", cookie.split(";", 2)[0]);
        }

        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.connect();

        // Upload the signed bytes
        os = conn.getOutputStream();
        os.write(data);
        os.flush();
        os.close();

        // Receive the signed document
        is = conn.getInputStream();
        FileOutputStream fos = new FileOutputStream(DEST + RESULT_FILES[0]);
        data = new byte[256];
        while ((read = is.read(data)) != -1) {
            fos.write(data, 0, read);
        }

        is.close();
        fos.flush();
        fos.close();
    }

}
