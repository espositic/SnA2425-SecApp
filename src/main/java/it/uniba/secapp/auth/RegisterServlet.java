package it.uniba.secapp.auth;

import it.uniba.secapp.dao.UserDao;
import it.uniba.secapp.model.User;
import it.uniba.secapp.util.PasswordUtil;
import it.uniba.secapp.util.TikaUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.nio.file.*;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
@MultipartConfig
public class RegisterServlet extends HttpServlet {

    private Path profileDir;
    private static final long MAX_PIC_SIZE = 2 * 1024 * 1024; // 2MB

    @Override
    public void init() throws ServletException {
        String home = System.getProperty("user.home");
        profileDir = Paths.get(home, "secapp_uploads", "profile_pics");
        try {
            Files.createDirectories(profileDir);
        } catch (IOException e) {
            throw new ServletException("Impossibile creare cartella immagini profilo", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email = req.getParameter("email");
        String pass1 = req.getParameter("password");
        String pass2 = req.getParameter("confirmPassword");
        Part picPart = req.getPart("profilePic");

        if (email == null || pass1 == null || pass2 == null ||
                email.isEmpty() || pass1.isEmpty() || pass2.isEmpty()) {
            req.setAttribute("error", "Compila tutti i campi obbligatori.");
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }

        if (!pass1.equals(pass2)) {
            req.setAttribute("error", "Le password non coincidono.");
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }

        // Controllo complessità password
        if (!pass1.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$")) {
            req.setAttribute("error", "Password troppo debole. Usa almeno 8 caratteri, con maiuscola, minuscola, numero e simbolo.");
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }

        String profilePath = null;

        if (picPart != null && picPart.getSize() > 0) {
            if (picPart.getSize() > MAX_PIC_SIZE) {
                req.setAttribute("error", "Immagine troppo grande (max 2MB).");
                req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
                return;
            }

            String filename = Paths.get(picPart.getSubmittedFileName()).getFileName().toString().toLowerCase();

            if (!(filename.endsWith(".png") || filename.endsWith(".jpg") || filename.endsWith(".jpeg"))) {
                req.setAttribute("error", "Formato immagine non valido (solo PNG/JPG).");
                req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
                return;
            }

            // Validazione MIME con Tika
            try (InputStream in = picPart.getInputStream()) {
                String mime = TikaUtil.detectMime(in);
                if (!(mime.equals("image/png") || mime.equals("image/jpeg"))) {
                    req.setAttribute("error", "Tipo MIME non valido: " + mime);
                    req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
                    return;
                }
            }

            // Salvataggio file
            Path dest = profileDir.resolve(System.currentTimeMillis() + "_" + filename);
            try (InputStream in = picPart.getInputStream()) {
                Files.copy(in, dest);
            }
            profilePath = dest.toString();
        }

        User u = new User();
        u.setEmail(email);
        u.setPasswordPlain(pass1); // verrà hashata in UserDao
        u.setProfilePicPath(profilePath);

        UserDao dao = new UserDao();
        try {
            if (dao.create(u)) {
                resp.sendRedirect(req.getContextPath() + "/login?registered=1");
            } else {
                req.setAttribute("error", "Registrazione fallita.");
                req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
