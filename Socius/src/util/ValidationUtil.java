package util;

import java.util.regex.Pattern;

public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern USERNAME_PATTERN =
        Pattern.compile("^[A-Za-z0-9_]{3,30}$");
    private static final Pattern PHONE_PATTERN =
        Pattern.compile("^[0-9+\\-() ]{8,20}$");
    private static final Pattern STRONG_PASSWORD_PATTERN =
        Pattern.compile("^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%!]).{8,}$");

    private ValidationUtil() {
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && PHONE_PATTERN.matcher(phoneNumber).matches();
    }

    public static boolean isStrongPassword(String password) {
        return password != null && STRONG_PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean hasLengthBetween(String value, int min, int max) {
        if (value == null) {
            return false;
        }

        int length = value.trim().length();
        return length >= min && length <= max;
    }

    public static boolean isPresent(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
