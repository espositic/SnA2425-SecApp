package it.uniba.secapp.web;

import it.uniba.secapp.dao.RememberTokenDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name="LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long uid = (Long) req.getSession().getAttribute("authUserId");
        if (uid != null) {
            new RememberTokenDao().deleteByUser(uid);
        }
        req.getSession().invalidate();

        // cancella cookie REMEMBER
        String del = "REMEMBER=; Max-Age=0; Path=/; Secure; HttpOnly; SameSite=Strict";
        resp.addHeader("Set-Cookie", del);

        resp.sendRedirect(req.getContextPath()+"/login.jsp?ok=Logout%20effettuato");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}
