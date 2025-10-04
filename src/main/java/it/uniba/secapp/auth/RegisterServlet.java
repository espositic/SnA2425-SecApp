package it.uniba.secapp.auth;

import it.uniba.secapp.dao.UserDao;
import it.uniba.secapp.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Sprint 1: registrazione base.
 * NOTA: password in chiaro SOLO per MVP; in Sprint 3 useremo hash + salt.
 */
@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email = req.getParameter("email");
        String pass = req.getParameter("password");
        String confirm = req.getParameter("confirm_password");

        if (email == null || pass == null || confirm == null
                || email.isEmpty() || pass.isEmpty()) {
            req.setAttribute("error", "Compila tutti i campi.");
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }
        if (!pass.equals(confirm)) {
            req.setAttribute("error", "Le password non coincidono.");
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }

        UserDao dao = new UserDao();
        try {
            if (dao.findByEmail(email) != null) {
                req.setAttribute("error", "Email già registrata.");
                req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
                return;
            }
            User u = new User();
            u.setEmail(email);
            u.setPasswordPlain(pass); // S1 solo per MVP
            dao.create(u);

            // redirect al login con messaggio
            resp.sendRedirect(req.getContextPath() + "/login?registered=1");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
