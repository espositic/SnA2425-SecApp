package it.uniba.secapp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * HelloServlet - verifica container e classpath.
 * Sprint 0: solo check ambiente. (Tecnologie allineate: Java 8, Servlet/JSP, Tomcat 9)
 */
@WebServlet(name = "HelloServlet", urlPatterns = {"/hello"})
public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/plain; charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.println("Hello from SecApp (Java 8 / Tomcat 9)!");
        }
    }
}
