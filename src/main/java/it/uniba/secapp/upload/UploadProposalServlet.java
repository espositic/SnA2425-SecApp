package it.uniba.secapp.upload;

import it.uniba.secapp.dao.ProposalDao;
import it.uniba.secapp.proposals.Helper;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

/**
 * Sprint 1: upload base di file .txt (controllo solo sull'estensione).
 * Sprint 2: aggiungeremo Apache Tika per verificare il MIME reale + sanitizzazione contenuti.
 */
@WebServlet(name = "UploadProposalServlet", urlPatterns = {"/upload"})
@MultipartConfig
public class UploadProposalServlet extends HttpServlet {

    private Path uploadDir;

    @Override
    public void init() throws ServletException {
        // cartella base per upload (sviluppo): ~/secapp_uploads
        String home = System.getProperty("user.home");
        uploadDir = Paths.get(home, "secapp_uploads");
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new ServletException("Impossibile creare la cartella di upload", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userEmail") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String title = req.getParameter("title");
        Part filePart = req.getPart("proposal");

        if (title == null || title.isEmpty() || filePart == null || filePart.getSize() == 0) {
            req.setAttribute("error", "Inserisci titolo e seleziona un file .txt");
            req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
            return;
        }

        // Controllo base S1: estensione .txt (in S2 useremo Tika per il controllo serio)
        String submitted = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        if (!submitted.toLowerCase().endsWith(".txt")) {
            req.setAttribute("error", "Carica un file con estensione .txt");
            req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
            return;
        }

        // Salvataggio su disco con nome "timestamp_originale"
        Path dest = uploadDir.resolve(System.currentTimeMillis() + "_" + submitted);
        try (InputStream in = filePart.getInputStream()) {
            Files.copy(in, dest);
        }

        // Salvataggio su DB
        String email = (String) session.getAttribute("userEmail");
        int userId = Helper.findUserIdByEmail(email);

        ProposalDao dao = new ProposalDao();
        try {
            dao.insert(userId, title, dest.toString(), false);
            resp.sendRedirect(req.getContextPath() + "/proposals?uploaded=1");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
