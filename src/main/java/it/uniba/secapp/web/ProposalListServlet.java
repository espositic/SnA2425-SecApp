package it.uniba.secapp.web;

import it.uniba.secapp.dao.ProposalDao;
import it.uniba.secapp.model.Proposal;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

@WebServlet(name="ProposalListServlet", urlPatterns = {"/proposals"})
public class ProposalListServlet extends HttpServlet {

    private static final int MAX_CHARS = 64 * 1024; // leggi fino a 64KB per proposta

    @Override
    protected void doGet(HttpServletRequest req, javax.servlet.http.HttpServletResponse resp)
            throws ServletException, IOException {

        List<Proposal> proposals = new ProposalDao().listAll();

        for (Proposal p : proposals) {
            String path = p.getFilePath();
            if (path == null || path.isEmpty()) {
                p.setFileContent(null);
                continue;
            }
            try {
                byte[] bytes = Files.readAllBytes(Paths.get(path));
                String text = new String(bytes, StandardCharsets.UTF_8);
                if (text.length() > MAX_CHARS) {
                    text = text.substring(0, MAX_CHARS) + "\n\n[...contenuto troncato per visualizzazione...]";
                }
                p.setFileContent(text);
            } catch (Exception ex) {
                p.setFileContent("[Errore lettura file]");
            }
        }

        req.setAttribute("proposals", proposals);
        req.getRequestDispatcher("/proposals.jsp").forward(req, resp);
    }
}
