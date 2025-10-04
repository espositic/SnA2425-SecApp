package it.uniba.secapp.upload;

import it.uniba.secapp.dao.ProposalDao;
import it.uniba.secapp.proposals.Helper;
import it.uniba.secapp.util.TikaUtil;

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
 * Sprint 2:
 * - Valida il MIME reale con Apache Tika (deve essere text/plain)
 * - Legge un sample e blocca contenuti sospetti (HTML/script)
 * - Mantiene limite semplice: estensione .txt + MIME text/plain
 */
@WebServlet(name = "UploadProposalServlet", urlPatterns = {"/upload"})
@MultipartConfig
public class UploadProposalServlet extends HttpServlet {

    private Path uploadDir;
    private static final long MAX_SIZE_BYTES = 512 * 1024; // 512 KB (esempio)

    @Override
    public void init() throws ServletException {
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

        if (filePart.getSize() > MAX_SIZE_BYTES) {
            req.setAttribute("error", "File troppo grande (max 512KB per Sprint 2).");
            req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
            return;
        }

        String submittedName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        if (!submittedName.toLowerCase().endsWith(".txt")) {
            req.setAttribute("error", "Estensione non valida. Carica un file .txt");
            req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
            return;
        }

        // 1) Verifica MIME reale con Tika
        try (InputStream probe = filePart.getInputStream()) {
            String mime = TikaUtil.detectMime(probe);
            if (!"text/plain".equalsIgnoreCase(mime)) {
                req.setAttribute("error", "Tipo di file non valido (MIME rilevato: " + mime + "). Serve text/plain.");
                req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
                return;
            }
        }

        // 2) Ispezione contenuto (sample)
        try (InputStream sample = filePart.getInputStream()) {
            String textSample = TikaUtil.readTextSample(sample, 64 * 1024); // leggi fino a 64KB
            if (TikaUtil.looksMaliciousText(textSample)) {
                req.setAttribute("error", "Contenuto sospetto (HTML/script non ammesso).");
                req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
                return;
            }
        }

        // 3) Salvataggio su disco
        Path dest = uploadDir.resolve(System.currentTimeMillis() + "_" + submittedName);
        try (InputStream in = filePart.getInputStream()) {
            Files.copy(in, dest);
        }

        // 4) Inserimento a DB
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
