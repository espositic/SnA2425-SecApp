package it.uniba.secapp.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * Gestione password sicure con PBKDF2.
 * Sprint 3: sostituisce l'uso di password_plain con password_hash.
 */
public final class PasswordUtil {

    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256; // bit
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final SecureRandom RAND = new SecureRandom();

    private PasswordUtil() {}

    public static String hashPassword(char[] password) {
        byte[] salt = new byte[16];
        RAND.nextBytes(salt);

        byte[] hash = pbkdf2(password, salt, ITERATIONS, KEY_LENGTH);
        return ITERATIONS + ":" + Base64.getEncoder().encodeToString(salt) + ":" +
                Base64.getEncoder().encodeToString(hash);
    }

    public static boolean validatePassword(char[] password, String stored) {
        String[] parts = stored.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = Base64.getDecoder().decode(parts[1]);
        byte[] hash = Base64.getDecoder().decode(parts[2]);

        byte[] testHash = pbkdf2(password, salt, iterations, hash.length * 8);
        if (hash.length != testHash.length) return false;

        int diff = 0;
        for (int i = 0; i < hash.length; i++) {
            diff |= hash[i] ^ testHash[i];
        }
        return diff == 0;
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}
