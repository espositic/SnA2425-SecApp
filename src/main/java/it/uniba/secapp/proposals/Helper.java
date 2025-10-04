package it.uniba.secapp.proposals;

import it.uniba.secapp.util.Db;

import java.sql.*;

/**
 * Helper MVP Sprint 1 per ottenere userId da email.
 * Nello Sprint 2/3 sposteremo questa logica dentro UserDao.
 */
public final class Helper {

    private Helper() {}

    public static int findUserIdByEmail(String email) {
        String sql = "SELECT id FROM users WHERE email=?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new RuntimeException("Utente non trovato: " + email);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
