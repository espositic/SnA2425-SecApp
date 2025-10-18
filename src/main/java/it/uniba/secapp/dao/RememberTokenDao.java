package it.uniba.secapp.dao;

import it.uniba.secapp.util.DB;

import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Base64;

public class RememberTokenDao {
    private static final SecureRandom RNG = new SecureRandom();

    public static class TokenPair {
        public final String selector;     // in chiaro nel cookie
        public final String validator;    // in chiaro nel cookie (ma hash nel DB)
        public TokenPair(String s, String v){ this.selector=s; this.validator=v; }
    }

    public TokenPair createToken(long userId, int daysValid) {
        byte[] sel = new byte[12];  // 16 char base64url circa
        byte[] val = new byte[32];
        RNG.nextBytes(sel);
        RNG.nextBytes(val);
        String selector = Base64.getUrlEncoder().withoutPadding().encodeToString(sel);
        String validator = Base64.getUrlEncoder().withoutPadding().encodeToString(val);

        String validatorHash = org.mindrot.jbcrypt.BCrypt.hashpw(validator, org.mindrot.jbcrypt.BCrypt.gensalt(12));
        LocalDateTime exp = LocalDateTime.now().plusDays(daysValid);

        String sql = "INSERT INTO remember_tokens(user_id, selector, validator_hash, expires_at) VALUES (?,?,?,?)";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, selector);
            ps.setString(3, validatorHash);
            ps.setTimestamp(4, Timestamp.valueOf(exp));
            ps.executeUpdate();
            return new TokenPair(selector, validator);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Long validateAndConsume(String selector, String validator) {
        String sql = "SELECT id, user_id, validator_hash, expires_at FROM remember_tokens WHERE selector = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, selector);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                long id = rs.getLong("id");
                long userId = rs.getLong("user_id");
                String hash = rs.getString("validator_hash");
                Timestamp exp = rs.getTimestamp("expires_at");
                if (exp == null || exp.toInstant().isBefore(java.time.Instant.now())) {
                    deleteById(id);
                    return null;
                }
                boolean ok = org.mindrot.jbcrypt.BCrypt.checkpw(validator, hash);
                if (!ok) {
                    // token rubato o manomesso: cancella
                    deleteById(id);
                    return null;
                }
                // one-time: cancella dopo l’uso per limitare replay
                deleteById(id);
                return userId;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteByUser(long userId) {
        String sql = "DELETE FROM remember_tokens WHERE user_id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteById(long id) {
        String sql = "DELETE FROM remember_tokens WHERE id = ?";
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
