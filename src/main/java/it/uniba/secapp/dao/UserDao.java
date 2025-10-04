package it.uniba.secapp.dao;

import it.uniba.secapp.model.User;
import it.uniba.secapp.util.Db;

import java.sql.*;

/**
 * UserDao — Data Access Object per la tabella users.
 *
 * Sprint 1:
 * - gestisce password in chiaro (solo per MVP)
 * - in Sprint 3 sostituiremo con password hash + salt
 */
public class UserDao {

    // Crea un nuovo utente
    public boolean create(User u) throws SQLException {
        String sql = "INSERT INTO users(email, password_plain) VALUES(?, ?)";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getEmail());
            ps.setString(2, u.getPasswordPlain());
            return ps.executeUpdate() == 1;
        }
    }

    // Trova un utente a partire dall'email
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT id, email, password_plain FROM users WHERE email=?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setEmail(rs.getString("email"));
                    u.setPasswordPlain(rs.getString("password_plain"));
                    return u;
                }
                return null;
            }
        }
    }

    // Controlla se login è valido (per ora confronto diretto in chiaro)
    public boolean validateLogin(String email, String passwordPlain) throws SQLException {
        User u = findByEmail(email);
        return u != null && u.getPasswordPlain().equals(passwordPlain);
    }
}
