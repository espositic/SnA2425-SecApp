package it.uniba.secapp.filter;

import it.uniba.secapp.dao.RememberTokenDao;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;

public class AuthFilter implements Filter {

    // Pagine/endpoint pubblici (non richiedono autenticazione)
    private static final String[] PUBLIC_PATHS = new String[] {
            "/",                 // homepage pubblica; rimuovi se vuoi proteggerla
            "/index.jsp",
            "/login.jsp",
            "/register.jsp",
            "/login",
            "/register",
            "/logout",           // lasciamo pubblico: invalida se sessione presente
            "/favicon.ico"
            // Aggiungi qui eventuali asset pubblici: es. "/assets/", "/css/", "/js/"
    };

    @Override public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // ===== Security headers (hardening) =====
        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("X-Frame-Options", "DENY");
        resp.setHeader("Referrer-Policy", "no-referrer");
        resp.setHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        resp.setHeader("Pragma", "no-cache");
        // CSP minimale (consenti solo risorse locali; CSS inline facoltativo)
        resp.setHeader("Content-Security-Policy",
                "default-src 'self'; img-src 'self' data:; style-src 'self' 'unsafe-inline'; base-uri 'self'; form-action 'self'");
        // HSTS (solo su HTTPS)
        if (req.isSecure()) {
            resp.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");
        }

        // ===== Lascia passare risorse pubbliche =====
        if (isPublic(req)) {
            chain.doFilter(request, response);
            return;
        }

        // ===== Sessione già valida? =====
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("authUserId") != null) {
            secureSessionCookieFallback(req, resp); // rinforza JSESSIONID
            chain.doFilter(request, response);
            return;
        }

        // ===== Auto-login via cookie REMEMBER (selector:validator) =====
        String remember = readCookie(req, "REMEMBER");
        if (remember != null && remember.contains(":")) {
            String[] parts = remember.split(":", 2);
            String selector  = parts[0];
            String validator = parts[1];

            RememberTokenDao tdao = new RememberTokenDao();
            Long userId = tdao.validateAndConsume(selector, validator); // one-time token
            if (userId != null) {
                // Crea nuova sessione e ruota l'ID (session fixation protection)
                HttpSession newSess = req.getSession(true);
                try { req.changeSessionId(); } catch (NoSuchMethodError ignored) {
                    newSess.invalidate(); newSess = req.getSession(true);
                }
                newSess.setAttribute("authUserId", userId);
                newSess.setMaxInactiveInterval(15 * 60);

                // Rilascia NUOVO token remember (rotazione, validità 14 giorni)
                RememberTokenDao.TokenPair pair = tdao.createToken(userId, 14);
                String cookieValue = pair.selector + ":" + pair.validator;
                int maxAge = 14 * 24 * 60 * 60;
                String c = "REMEMBER=" + cookieValue
                        + "; Max-Age=" + maxAge
                        + "; Path=/"
                        + "; Secure"
                        + "; HttpOnly"
                        + "; SameSite=Strict";
                resp.addHeader("Set-Cookie", c);

                secureSessionCookieFallback(req, resp);
                chain.doFilter(request, response);
                return;
            } else {
                // Token non valido/scaduto → elimina cookie
                resp.addHeader("Set-Cookie", "REMEMBER=; Max-Age=0; Path=/; Secure; HttpOnly; SameSite=Strict");
            }
        }

        // ===== Non autenticato: redirect al login, preservando la destinazione =====
        String target = req.getRequestURI();
        String qs = req.getQueryString();
        if (qs != null) target += "?" + qs;

        String redirect = req.getContextPath() + "/login.jsp?err=" +
                URLEncoder.encode("Devi autenticarti", "UTF-8") +
                "&next=" + URLEncoder.encode(target, "UTF-8");

        resp.sendRedirect(redirect);
    }

    @Override public void destroy() {}

    // ===== Helpers =====

    private boolean isPublic(HttpServletRequest req) {
        String ctx = req.getContextPath();
        String uri = req.getRequestURI();
        String path = uri.startsWith(ctx) ? uri.substring(ctx.length()) : uri;
        if (path.isEmpty()) path = "/";
        for (String pub : PUBLIC_PATHS) {
            if (pub.endsWith("/")) {
                if (path.startsWith(pub)) return true;
            } else {
                if (path.equals(pub) || path.startsWith(pub + "/")) return true;
            }
        }
        return false;
    }

    private String readCookie(HttpServletRequest req, String name) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }

    /** Aggiunge attributi sicuri al cookie di sessione come fallback. */
    private void secureSessionCookieFallback(HttpServletRequest req, HttpServletResponse resp) {
        if (!req.isSecure()) return; // solo su HTTPS ha senso Secure/HSTS
        Cookie[] cookies = req.getCookies();
        if (cookies == null) return;
        for (Cookie c : cookies) {
            if ("JSESSIONID".equals(c.getName())) {
                String v = c.getValue();
                // Path=/ per coprire tutta l'app; SameSite=Strict per ridurre CSRF
                resp.addHeader("Set-Cookie", "JSESSIONID=" + v + "; Path=/; Secure; HttpOnly; SameSite=Strict");
                break;
            }
        }
    }
}
