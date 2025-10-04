package it.uniba.secapp.proposals;

import it.uniba.secapp.dao.ProposalDao;
import it.uniba.secapp.model.Proposal;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

@WebServlet(name = "ProposalDetailsServlet", urlPatterns = {"/proposal"})
public class ProposalDetailsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession s = req.getSession(false);
        if (s == null || s.getAttribute("userEmail") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        String idStr = req.getParameter("id");
        if (idStr == null) {
            resp.sendRedirect(req.getContextPath() + "/proposals");
            return;
        }
        int id = Integer.parseInt(idStr);
        int userId = Helper.findUserIdByEmail((String) s.getAttribute("userEmail"));

        ProposalDao dao = new ProposalDao();
        try {
            Proposal p = dao.findByIdAndUser(id, userId);
            if (p == null) {
                resp.sendRedirect(req.getContextPath() + "/proposals");
                return;
            }
            String content = new String(
                    Files.readAllBytes(Paths.get(p.getBodyTextPath())),
                    StandardCharsets.UTF_8
            );
            req.setAttribute("proposal", p);
            req.setAttribute("content", content); // S2: escape output
            req.getRequestDispatcher("/WEB-INF/views/proposal.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
