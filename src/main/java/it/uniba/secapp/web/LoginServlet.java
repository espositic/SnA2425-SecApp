package it.uniba.secapp.web;

import it.uniba.secapp.dao.RememberTokenDao;
import it.uniba.secapp.dao.UserDao;
import it.uniba.secapp.model.User;
import it.uniba.secapp.util.CookieUtil;
import it.uniba.secapp.util.ValidationUtil;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet(name="LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String email = req.getParameter("email");
        String pwd   = req.getParameter("password");
        boolean remember = "1".equals(req.getParameter("remember"));

        if (!ValidationUtil.isEmail(email) || pwd == null || pwd.isEmpty()) {
            resp.sendRedirect(req.getContextPath()+"/login.jsp?err="+ URLEncoder.encode("Credenziali non valide","UTF-8"));
            return;
        }

        UserDao userDao = new UserDao();
        User u = userDao.findByEmail(email);
        if (u == null || u.getPwdHash() == null || !BCrypt.checkpw(pwd, u.getPwdHash())) {
            resp.sendRedirect(req.getContextPath()+"/login.jsp?err="+ URLEncoder.encode("Email o password errate","UTF-8"));
            return;
        }

        // Session fixation prevention: rigenera ID sessione
        HttpSession session = req.getSession(true);
        try {
            req.changeSessionId();
        } catch (NoSuchMethodError ignored) {
            // fallback: invalidate + nuova (per vecchie versioni)
            session.invalidate();
            session = req.getSession(true);
        }
        session.setAttribute("authUserId", u.getId());
        session.setAttribute("authEmail", u.getEmail());
        session.setMaxInactiveInterval(15 * 60); // 15 min

        // Remember-me opzionale con token (selector+validator)
        if (remember) {
            RememberTokenDao tdao = new RememberTokenDao();
            RememberTokenDao.TokenPair pair = tdao.createToken(u.getId(), 14); // 14 giorni

            String cookieValue = pair.selector + ":" + pair.validator;

            // Header esplicito per SameSite=Strict + Secure + HttpOnly
            int maxAge = 14 * 24 * 60 * 60;
            String c = "REMEMBER=" + cookieValue
                    + "; Max-Age=" + maxAge
                    + "; Path=/"
                    + "; Secure"
                    + "; HttpOnly"
                    + "; SameSite=Strict";
            resp.addHeader("Set-Cookie", c);
        }

        resp.sendRedirect(req.getContextPath()+"/index.jsp?ok="+ URLEncoder.encode("Bentornato!","UTF-8"));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath()+"/login.jsp");
    }
}
