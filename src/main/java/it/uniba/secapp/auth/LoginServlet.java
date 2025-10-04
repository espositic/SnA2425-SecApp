package it.uniba.secapp.auth;

import it.uniba.secapp.dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Sprint 1: login base con HttpSession (nessun remember-me ancora).
 * Sprint 2 aggiungerà gestione cookie sicuri e hardening sessione.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email = req.getParameter("email");
        String pass  = req.getParameter("password");

        if (email == null || pass == null || email.isEmpty() || pass.isEmpty()) {
            req.setAttribute("error", "Inserisci email e password.");
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            return;
        }

        UserDao dao = new UserDao();
        try {
            if (dao.validateLogin(email, pass)) {
                HttpSession session = req.getSession(true);
                session.setAttribute("userEmail", email);
                resp.sendRedirect(req.getContextPath() + "/dashboard");
            } else {
                req.setAttribute("error", "Credenziali non valide.");
                req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
