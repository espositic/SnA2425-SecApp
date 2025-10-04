package it.uniba.secapp.proposals;

import it.uniba.secapp.dao.ProposalDao;
import it.uniba.secapp.model.Proposal;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "ProposalsServlet", urlPatterns = {"/proposals"})
public class ProposalsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession s = req.getSession(false);
        if (s == null || s.getAttribute("userEmail") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        int userId = Helper.findUserIdByEmail((String) s.getAttribute("userEmail"));
        ProposalDao dao = new ProposalDao();
        try {
            List<Proposal> list = dao.listByUser(userId);
            req.setAttribute("proposals", list);
            req.getRequestDispatcher("/WEB-INF/views/proposals.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
