package it.uniba;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Minimal Hello servlet to verify Tomcat + WAR deployment works.
 */
@WebServlet(name = "HelloServlet", urlPatterns = {"/hello"})
public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>Hello</title></head><body>");
            out.println("<h1>Setup OK ✅</h1>");
            out.println("<p>Tomcat + IntelliJ + Maven funzionano. Prossimo passo: M1.</p>");
            out.println("</body></html>");
        }
    }
}