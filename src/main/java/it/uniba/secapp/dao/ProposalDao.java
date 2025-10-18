package it.uniba.secapp.dao;

import it.uniba.secapp.model.Proposal;
import it.uniba.secapp.util.DB;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProposalDao {

    public long insert(Proposal p) {
        String sql = "INSERT INTO proposals(title, created_by, file_path) VALUES (?,?,?)";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getTitle());
            ps.setLong(2, p.getCreatedBy());
            ps.setString(3, p.getFilePath());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Proposal> listAll() {
        String sql = "SELECT id, title, file_path, created_by, created_at " +
                "FROM proposals ORDER BY created_at DESC";
        List<Proposal> list = new ArrayList<>();
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Proposal p = new Proposal();
                p.setId(rs.getLong("id"));
                p.setTitle(rs.getString("title"));
                p.setFilePath(rs.getString("file_path"));
                p.setCreatedBy(rs.getLong("created_by"));
                Timestamp ts = rs.getTimestamp("created_at");
                p.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);
                list.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}
