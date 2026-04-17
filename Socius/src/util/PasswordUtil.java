package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordUtil {

    private PasswordUtil() {
    }

    public static String hash(String rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("Password cannot be null.");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();

            for (byte value : hashed) {
                builder.append(String.format("%02x", value));
            }

            return builder.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available.", exception);
        }
    }

    public static boolean verify(String rawPassword, String storedHash) {
        if (rawPassword == null || storedHash == null) {
            return false;
        }

        return hash(rawPassword).equalsIgnoreCase(storedHash);
    }
}
