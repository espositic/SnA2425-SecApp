package it.uniba.secapp.dao;

import it.uniba.secapp.model.Proposal;
import it.uniba.secapp.util.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ProposalDao — Data Access Object per la tabella proposals.
 *
 * Sprint 1:
 * - inserisce proposte legate a un utente
 * - restituisce elenco delle proposte di un utente
 * - recupera una singola proposta
 *
 * Sprint 2/3:
 * - aggiungeremo validazioni con Apache Tika e controlli di sicurezza
 */
public class ProposalDao {

    // Inserisce una nuova proposta
    public boolean insert(int userId, String title, String path, boolean isPublic) throws SQLException {
        String sql = "INSERT INTO proposals(user_id, title, body_text_path, is_public) VALUES(?,?,?,?)";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, title);
            ps.setString(3, path);
            ps.setBoolean(4, isPublic);
            return ps.executeUpdate() == 1;
        }
    }

    // Restituisce tutte le proposte di un utente
    public List<Proposal> listByUser(int userId) throws SQLException {
        String sql = "SELECT id, user_id, title, body_text_path, is_public, created_at " +
                "FROM proposals WHERE user_id=? ORDER BY created_at DESC";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Proposal> list = new ArrayList<>();
                while (rs.next()) {
                    Proposal p = new Proposal();
                    p.setId(rs.getInt("id"));
                    p.setUserId(rs.getInt("user_id"));
                    p.setTitle(rs.getString("title"));
                    p.setBodyTextPath(rs.getString("body_text_path"));
                    p.setPublic(rs.getBoolean("is_public"));
                    // createdAt non lo leggiamo per semplicità ora
                    list.add(p);
                }
                return list;
            }
        }
    }

    // Recupera una singola proposta per id e utente
    public Proposal findByIdAndUser(int id, int userId) throws SQLException {
        String sql = "SELECT id, user_id, title, body_text_path, is_public " +
                "FROM proposals WHERE id=? AND user_id=?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Proposal p = new Proposal();
                    p.setId(rs.getInt("id"));
                    p.setUserId(rs.getInt("user_id"));
                    p.setTitle(rs.getString("title"));
                    p.setBodyTextPath(rs.getString("body_text_path"));
                    p.setPublic(rs.getBoolean("is_public"));
                    return p;
                }
                return null;
            }
        }
    }
}
