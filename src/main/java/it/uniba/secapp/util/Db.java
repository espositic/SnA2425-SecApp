package it.uniba.secapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Sprint 1 — Connessione semplice JDBC (senza SSL).
 * In Sprint 3 abiliteremo SSL/TLS e parametri di sicurezza.
 */
public final class Db {
    private static final String URL  =
            "jdbc:mysql://localhost:3306/secapp?serverTimezone=UTC";
    private static final String USER = "secapp_user"; // <-- metti le tue credenziali
    private static final String PASS = "UserPass!";   // <-- metti le tue credenziali

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // carica driver MySQL
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL non trovato", e);
        }
    }

    private Db() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
