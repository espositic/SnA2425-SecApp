package it.uniba.secapp.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {
    public static void addCookie(HttpServletResponse resp, String name, String value, int maxAgeSeconds, boolean secure, boolean httpOnly) {
        Cookie c = new Cookie(name, value);
        c.setPath("/");              // valido per tutta l’app
        c.setMaxAge(maxAgeSeconds);  // scadenza
        c.setSecure(secure);         // solo via HTTPS
        c.setHttpOnly(httpOnly);     // non leggibile da JS
        resp.addCookie(c);
    }
}
