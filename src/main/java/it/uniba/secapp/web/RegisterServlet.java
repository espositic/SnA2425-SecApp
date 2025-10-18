package it.uniba.secapp.web;

import it.uniba.secapp.dao.UserDao;
import it.uniba.secapp.model.User;
import it.uniba.secapp.util.ValidationUtil;
import org.apache.tika.Tika;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@WebServlet(name="RegisterServlet", urlPatterns = {"/register"})
@MultipartConfig(maxFileSize = 2 * 1024 * 1024) // 2MB
public class RegisterServlet extends HttpServlet {

    private final Tika tika = new Tika();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String email = req.getParameter("email");
        String pwd = req.getParameter("password");
        String confirm = req.getParameter("confirm");
        Part image = req.getPart("image");

        // Validazioni base
        if (!ValidationUtil.isEmail(email)) {
            resp.sendRedirect(req.getContextPath()+"/register.jsp?err=Email%20non%20valida");
            return;
        }
        if (!ValidationUtil.isStrongPassword(pwd)) {
            resp.sendRedirect(req.getContextPath()+"/register.jsp?err=Password%20debole%3A%20min%208%2C%201%20maiusc%2C%201%20minuscola%2C%201%20numero");
            return;
        }
        if (confirm == null || !pwd.equals(confirm)) {
            resp.sendRedirect(req.getContextPath()+"/register.jsp?err=Le%20password%20non%20coincidono");
            return;
        }
        if (image == null || image.getSize() == 0) {
            resp.sendRedirect(req.getContextPath()+"/register.jsp?err=Immagine%20obbligatoria");
            return;
        }

        // Verifica MIME reale con Tika
        String detected = tika.detect(image.getInputStream());
        if (!( "image/png".equalsIgnoreCase(detected) || "image/jpeg".equalsIgnoreCase(detected))) {
            resp.sendRedirect(req.getContextPath()+"/register.jsp?err=Formato%20immagine%20non%20ammesso");
            return;
        }

        // Cartella upload da web.xml (o fallback /tmp/...)
        String uploadDir = getServletContext().getInitParameter("upload.dir");
        if (uploadDir == null || uploadDir.trim().isEmpty()) {
            uploadDir = System.getProperty("java.io.tmpdir") + File.separator + "secapp-uploads" + File.separator + "profile";
        }
        File dir = new File(uploadDir);
        if (!dir.exists() && !dir.mkdirs()) {
            resp.sendRedirect(req.getContextPath()+"/register.jsp?err=Errore%20cartella%20upload");
            return;
        }

        // Nome file sicuro
        String ext = "image/png".equalsIgnoreCase(detected) ? ".png" : ".jpg";
        String safeName = UUID.randomUUID().toString().replace("-","") + ext;
        Path out = Paths.get(dir.getAbsolutePath(), safeName);

        // Salva su disco
        try {
            Files.copy(image.getInputStream(), out);
        } catch (IOException e) {
            resp.sendRedirect(req.getContextPath()+"/register.jsp?err=Salvataggio%20immagine%20fallito");
            return;
        }

        // Hash password con BCrypt
        String hash = BCrypt.hashpw(pwd, BCrypt.gensalt(12));

        // Inserisci in DB
        UserDao dao = new UserDao();
        if (dao.emailExists(email)) {
            try { Files.deleteIfExists(out); } catch (Exception ignore) {}
            resp.sendRedirect(req.getContextPath()+"/register.jsp?err=Email%20gi%C3%A0%20usata");
            return;
        }
        User u = new User(email, hash, out.toString());
        long id = dao.insert(u);
        if (id <= 0) {
            try { Files.deleteIfExists(out); } catch (Exception ignore) {}
            resp.sendRedirect(req.getContextPath()+"/register.jsp?err=Registrazione%20fallita");
            return;
        }

        resp.sendRedirect(req.getContextPath()+"/register.jsp?ok=Registrazione%20completata");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath()+"/register.jsp");
    }
}
