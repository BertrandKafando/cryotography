package ma.enset;


import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;

/*
 * to verify the integrity of a message 
 * use a Message Digest  or MAC
 * MAC is a small piece of data that is used to authenticate a message
 * Message Digest is a small piece of data gain after hashing Data that is used to verify the integrity of a message
 * 
 * HASHING is a one way function that is used to generate a small piece of data from a large piece of data
 * 
 * Algorithms are   MD5, SHA-1, SHA-256, SHA-384, SHA-512
 *  showTest("MD5");
 *   showTest("SHA-1");
 *   showTest("SHA-224");
 *   showTest("SHA-256");
 * 
 * Here creation of digest with iText library
 */
public class DigestMac  {
    protected byte[] digest;
    protected MessageDigest md;

    public DigestMac(String password, String algorithm, String provider) throws GeneralSecurityException {
        if (provider == null)
                md = MessageDigest.getInstance(algorithm);

        else 
                md = MessageDigest.getInstance(algorithm, provider);
    
        digest = md.digest(password.getBytes());   
    }

    public boolean checkPassword (String password){
        return Arrays.equals(this.digest, md.digest(password.getBytes()));
    }



    
}