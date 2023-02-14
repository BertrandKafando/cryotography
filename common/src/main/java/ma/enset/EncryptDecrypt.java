package ma.enset;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import javax.crypto.Cipher;

/**
 * Use a Message Digest or MAC to verify the integrity of a message protect the
 * integrity of a message
 * But someone can change the message and the digest in same time to make the
 * digest valid
 * So we need other way to protect the integrity of a message
 * we can add encryption to the message to protect the integrity of a message
 * Encryption is a way to protect the content of a message
 * there are two types of encryption : symmetric (one secret key) and asymmetric
 * (two keys : public and private)
 *
 * algorithms are : AES, DES, 3DES, Blowfish, RC4, RSA, DSA, ElGamal
 * 
 * here we will use Java KeyStore to store the keys and certificates for the
 * encryption and decryption
 * 
 * 
 * Interface Java Cipher
 */

public class EncryptDecrypt {
    private KeyStore ks;

    public EncryptDecrypt(String keyStorePath, String keyStorePassword)
            throws GeneralSecurityException, FileNotFoundException, IOException {
        this.initKeystore(keyStorePath, keyStorePassword);
    }

    public void initKeystore(String keyStorePath, String password)
            throws GeneralSecurityException, FileNotFoundException, IOException {
        this.ks = KeyStore.getInstance(KeyStore.getDefaultType());
        this.ks.load(new FileInputStream(keyStorePath), password.toCharArray());
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

    public byte[] encrypt(Key key, String message) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(message.getBytes());
    }

    public String decrypt(Key key, byte[] cipherText) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(cipherText));
    }

    public static void main(String[] args) throws FileNotFoundException, GeneralSecurityException, IOException {

        EncryptDecrypt app = new EncryptDecrypt("src/main/ressources/mykeystore.p12", "mypassword");

       // X509Certificate cert = app.getCertificate("myalias");
        PrivateKey privateKey = (PrivateKey) app.getPrivateKey("myalias", "mypassword");
        PublicKey publicKey = (PublicKey) app.getPublicKey("myalias");

        System.out.println("Let's encrypt 'secret message' with a public key");
        byte[] encrypted = app.encrypt(publicKey, "secret message");
        System.out.println("Encrypted message: "
                + new BigInteger(1, encrypted).toString(16));

        System.out.println("Let's decrypt it with the corresponding private key");
        String decrypted = app.decrypt(privateKey, encrypted);
        System.out.println(decrypted);

        System.out.println("You can also encrypt the message with a private key");
        encrypted = app.encrypt(privateKey, "secret message");
        System.out.println("Encrypted message: "
                + new BigInteger(1, encrypted).toString(16));
        System.out.println("Now you need the public key to decrypt it");
        decrypted = app.decrypt(publicKey, encrypted);
        System.out.println(decrypted);

    }
}
