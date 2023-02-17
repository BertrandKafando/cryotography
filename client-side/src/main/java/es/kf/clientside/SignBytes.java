package es.kf.clientside;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;

@WebServlet(name = "sign", value = "/sign" ,initParams = {
        @WebInitParam(name = "keystorepath", value = "/WEB-INF/keystore"),
        @WebInitParam(name = "keystoretype", value = "PKCS12"),
        @WebInitParam(name = "alias", value = "myalias"),
        @WebInitParam(name = "storepass", value = "mypassword"),
        @WebInitParam(name = "keypass", value = "mypassword")
})
public class SignBytes extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String keystorepath = getServletConfig().getInitParameter("keystorepath");
        String keystoretype = getServletConfig().getInitParameter("keystoretype");
        String alias = getServletConfig().getInitParameter("alias");
        String storepass = getServletConfig().getInitParameter("storepass");
        String keypass = getServletConfig().getInitParameter("keypass");
        try {
            System.out.println(keystorepath);
            InputStream is = getServletContext().getResourceAsStream(keystorepath);
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(is, storepass.toCharArray());
            PrivateKey pk = (PrivateKey) ks.getKey("myalias", "mypassword".toCharArray());
            Certificate cert = ks.getCertificate("mycert");
            Certificate[] chain = new Certificate[] { cert };
            System.out.println("Private key: " + pk);
            config.getServletContext().setAttribute("pk", pk);

        } catch (GeneralSecurityException | IOException e) {
            throw new ServletException(e);
        }


    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("SignServlet.jsp").forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/octet-stream");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = req.getInputStream();
        int read;
        byte[] data = new byte[256];
        while ((read = is.read(data, 0, data.length)) != -1) {
            baos.write(data, 0, read);
        }
        data = baos.toByteArray();
        try {
            Signature sig = Signature.getInstance("SHA256withRSA");
            PrivateKey pk = (PrivateKey)getServletContext().getAttribute("pk");
            sig.initSign(pk);
            sig.update(data);
            data = sig.sign();
            OutputStream os = resp.getOutputStream();
            os.write(data, 0, data.length);
            os.flush();
            os.close();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

}
