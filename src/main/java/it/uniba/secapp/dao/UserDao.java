package it.uniba.secapp.dao;

import it.uniba.secapp.model.User;
import it.uniba.secapp.util.Db;
import it.uniba.secapp.util.PasswordUtil;

import java.sql.*;

/**
 * UserDao — gestione utenti con password sicure (PBKDF2) e foto profilo.
 * Schema atteso (tabella users):
 *
 *   id INT PK AUTO_INCREMENT
 *   email VARCHAR(190) UNIQUE NOT NULL
 *   password_hash VARCHAR(255) NOT NULL
 *   profile_pic_path VARCHAR(255) NULL
 *   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
 */
public class UserDao {

    /** Crea un nuovo utente (hash PBKDF2) */
    public boolean create(User u) throws SQLException {
        final String sql =
                "INSERT INTO users(email, password_hash, profile_pic_path) VALUES(?, ?, ?)";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getEmail());
            // NB: u.getPasswordPlain() viene hashata qui. In seguito potrai rimuovere il plain dal model.
            String hash = PasswordUtil.hashPassword(u.getPasswordPlain().toCharArray());
            ps.setString(2, hash);
            ps.setString(3, u.getProfilePicPath());

            return ps.executeUpdate() == 1;
        }
    }

    /** Ritorna true se esiste un utente con questa email (utile per validazioni) */
    public boolean existsByEmail(String email) throws SQLException {
        final String sql = "SELECT 1 FROM users WHERE email = ? LIMIT 1";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Trova utente per email (senza esporre l’hash) */
    public User findByEmail(String email) throws SQLException {
        final String sql =
                "SELECT id, email, profile_pic_path, created_at FROM users WHERE email = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setEmail(rs.getString("email"));
                u.setProfilePicPath(rs.getString("profile_pic_path"));
                // created_at non mappato nel model per semplicità
                return u;
            }
        }
    }

    /** Valida il login confrontando la password fornita con l’hash salvato */
    public boolean validateLogin(String email, String passwordPlain) throws SQLException {
        final String sql = "SELECT password_hash FROM users WHERE email = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                String storedHash = rs.getString("password_hash");
                return PasswordUtil.validatePassword(passwordPlain.toCharArray(), storedHash);
            }
        }
    }

    /** Aggiorna solo il percorso della foto profilo */
    public boolean updateProfilePic(int userId, String path) throws SQLException {
        final String sql = "UPDATE users SET profile_pic_path = ? WHERE id = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, path);
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;
        }
    }

    /** Cambia password: genera nuovo hash PBKDF2 */
    public boolean changePassword(int userId, String newPlainPassword) throws SQLException {
        final String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        String newHash = PasswordUtil.hashPassword(newPlainPassword.toCharArray());
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;
        }
    }

    /** Trova ID utente per email (comodo per evitare helper separati) */
    public Integer findUserIdByEmail(String email) throws SQLException {
        final String sql = "SELECT id FROM users WHERE email = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        }
    }
}
