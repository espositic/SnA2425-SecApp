package it.uniba.secapp.util;

import java.util.regex.Pattern;

public class ValidationUtil {
    private static final Pattern EMAIL_RX =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    // Min 8, almeno 1 maiuscola, 1 minuscola, 1 numero
    private static final Pattern PWD_RX =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");

    public static boolean isEmail(String s){ return s != null && EMAIL_RX.matcher(s).matches(); }
    public static boolean isStrongPassword(String s){ return s != null && PWD_RX.matcher(s).matches(); }
}
