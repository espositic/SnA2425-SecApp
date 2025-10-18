package it.uniba.secapp.web;

import it.uniba.secapp.dao.ProposalDao;
import it.uniba.secapp.model.Proposal;
import org.apache.tika.Tika;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.*;
import java.util.UUID;

@WebServlet(name = "CreateProposalServlet", urlPatterns = {"/proposals/new"})
@MultipartConfig(maxFileSize = 2 * 1024 * 1024, maxRequestSize = 3 * 1024 * 1024)
public class CreateProposalServlet extends HttpServlet {
    private final Tika tika = new Tika();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/proposal_new.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        Long userId = (session != null) ? (Long) session.getAttribute("authUserId") : null;
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?err=" + enc("Devi autenticarti"));
            return;
        }

        String title = trimOrNull(req.getParameter("title"));
        if (title == null || title.length() < 3 || title.length() > 200) {
            resp.sendRedirect(req.getContextPath() + "/proposal_new.jsp?err=" + enc("Titolo 3-200 caratteri"));
            return;
        }

        Part filePart;
        try {
            filePart = req.getPart("textfile");
        } catch (IllegalStateException tooBig) {
            resp.sendRedirect(req.getContextPath() + "/proposal_new.jsp?err=" + enc("File troppo grande (max 2MB)"));
            return;
        }
        if (filePart == null || filePart.getSize() == 0) {
            resp.sendRedirect(req.getContextPath() + "/proposal_new.jsp?err=" + enc("Carica un file .txt"));
            return;
        }

        // Verifica MIME reale
        String mime = tika.detect(filePart.getInputStream());
        if (!"text/plain".equalsIgnoreCase(mime)) {
            resp.sendRedirect(req.getContextPath() + "/proposal_new.jsp?err=" + enc("Solo .txt (text/plain) ammessi"));
            return;
        }

        // Cartella upload
        String baseUpload = getServletContext().getInitParameter("upload.dir");
        if (baseUpload == null || baseUpload.trim().isEmpty()) {
            baseUpload = System.getProperty("java.io.tmpdir") + File.separator + "secapp-uploads";
        }
        File proposalsDir = new File(baseUpload, "proposals");
        if (!proposalsDir.exists() && !proposalsDir.mkdirs()) {
            resp.sendRedirect(req.getContextPath() + "/proposal_new.jsp?err=" + enc("Impossibile creare cartella upload"));
            return;
        }

        // Nome sicuro
        String safeName = UUID.randomUUID().toString().replace("-", "") + ".txt";
        Path out = Paths.get(proposalsDir.getAbsolutePath(), safeName);

        try {
            Files.copy(filePart.getInputStream(), out);
        } catch (IOException io) {
            resp.sendRedirect(req.getContextPath() + "/proposal_new.jsp?err=" + enc("Salvataggio file fallito"));
            return;
        }

        Proposal p = new Proposal(title, userId);
        p.setFilePath(out.toString());

        long id = new ProposalDao().insert(p);
        if (id <= 0) {
            try { Files.deleteIfExists(out); } catch (Exception ignore) {}
            resp.sendRedirect(req.getContextPath() + "/proposal_new.jsp?err=" + enc("Salvataggio fallito"));
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/proposals.jsp?ok=" + enc("Proposta creata"));
    }

    private static String trimOrNull(String s) { return s == null ? null : s.trim(); }
    private static String enc(String s) {
        try { return URLEncoder.encode(s, "UTF-8"); } catch (Exception e) { return s; }
    }
}
