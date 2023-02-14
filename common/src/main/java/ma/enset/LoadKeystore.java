package ma.enset;

import java.io.FileInputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;

public class LoadKeystore {

    KeyStore ks;

    public LoadKeystore (String path, String password) throws Exception{
     ks= KeyStore.getInstance(KeyStore.getDefaultType());
      ks.load( new FileInputStream(path), password.toCharArray());
    }

    public X509Certificate getCertificate(String alias) throws KeyStoreException {
        return (X509Certificate) this.ks.getCertificate(alias);
    }

    public Key getPublicKey(String alias) throws KeyStoreException {
        return getCertificate(alias).getPublicKey();
    }

    public Key getPrivateKey(String alias, String pass) throws GeneralSecurityException, KeyStoreException {
        return ks.getKey(alias, pass.toCharArray());
    }

    
}
