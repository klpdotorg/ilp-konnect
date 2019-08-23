package in.org.klp.ilpkonnect.constants;

import android.util.Base64;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Srilatha
 */

public class ApplicationConstants {

    public static String DEFAULT_PASSWORD = "rqHTnR464KUppi4";
    public static String encryptDecryptPassword = "KLP";
    //public static boolean isSyncing= false;
    /**
     * Code written for CR remove_login to encrypt a password
     */
    public static String encrypt() throws Exception {
        SecretKeySpec spec = generateKey(DEFAULT_PASSWORD);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, spec);

        byte[] bytes = cipher.doFinal(DEFAULT_PASSWORD.getBytes());
        String encryptedValue = Base64.encodeToString(bytes, Base64.DEFAULT);

        return encryptedValue;
    }

    /**
     * Code written for CR remove_login to decrypt the encrypted password
     */
    public static String decrypt(String toString1) throws Exception {
        SecretKeySpec spec = generateKey(toString1);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, spec);

        byte[] decodedValue = Base64.decode(toString1, Base64.DEFAULT);
        byte[] decodedValue1 = cipher.doFinal(decodedValue);
        String decryptedValue = new String(decodedValue1);
        return decryptedValue;
    }

    public static SecretKeySpec generateKey(String toString) throws Exception {
        final MessageDigest msgDigest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = encryptDecryptPassword.getBytes("UTF-8");
        msgDigest.update(bytes, 0, bytes.length);
        byte[] key = msgDigest.digest();
        SecretKeySpec spec = new SecretKeySpec(key, "AES");

        return spec;
    }
}
