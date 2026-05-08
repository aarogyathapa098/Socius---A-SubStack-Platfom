package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.Ban;

public class BanDAO {

    public void banUserFromCommunity(
        int userId,
        int communityId,
        int bannedBy,
        String reason,
        Timestamp expiresAt
    ) {
        String sql =
            "INSERT INTO bans (user_id, community_id, banned_by, reason, is_global, expires_at) "
                + "VALUES (?, ?, ?, ?, 0, ?)";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);
            statement.setInt(2, communityId);
            statement.setInt(3, bannedBy);
            statement.setString(4, reason);
            statement.setTimestamp(5, expiresAt);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to ban user from community.", exception);
        }
    }

    public void globalBanUser(int userId, int bannedBy, String reason) {
        String sql =
            "INSERT INTO bans (user_id, community_id, banned_by, reason, is_global, expires_at) "
                + "SELECT ?, NULL, ?, ?, 1, NULL "
                + "WHERE NOT EXISTS ("
                + "SELECT 1 FROM bans WHERE user_id = ? AND is_global = 1 "
                + "AND (expires_at IS NULL OR expires_at > CURRENT_TIMESTAMP)"
                + ")";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);
            statement.setInt(2, bannedBy);
            statement.setString(3, reason);
            statement.setInt(4, userId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to globally ban user.", exception);
        }
    }

    public void removeGlobalBansForUser(int userId) {
        String sql = "DELETE FROM bans WHERE user_id = ? AND is_global = 1";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to remove global bans.", exception);
        }
    }

    public void unbanUser(int banId) {
        String sql = "DELETE FROM bans WHERE ban_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, banId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to remove ban.", exception);
        }
    }

    public boolean isBannedFromCommunity(int userId, int communityId) {
        String sql =
            "SELECT 1 FROM bans WHERE user_id = ? AND community_id = ? AND (expires_at IS NULL OR expires_at > CURRENT_TIMESTAMP)";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);
            statement.setInt(2, communityId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to check community ban.", exception);
        }
    }

    public boolean isGloballyBanned(int userId) {
        String sql =
            "SELECT 1 FROM bans WHERE user_id = ? AND is_global = 1 "
                + "AND (expires_at IS NULL OR expires_at > CURRENT_TIMESTAMP)";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to check global ban.", exception);
        }
    }

    public List<Ban> getBansByCommunity(int communityId) {
        return new ArrayList<Ban>();
    }

    public List<Ban> getAllBans() {
        List<Ban> bans = new ArrayList<Ban>();
        String sql =
            "SELECT b.*, u.username, admin_user.username AS banned_by_username, c.name AS community_name "
                + "FROM bans b "
                + "JOIN users u ON b.user_id = u.user_id "
                + "JOIN users admin_user ON b.banned_by = admin_user.user_id "
                + "LEFT JOIN communities c ON b.community_id = c.community_id "
                + "ORDER BY b.created_at DESC";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                Ban ban = new Ban();
                ban.setBanId(resultSet.getInt("ban_id"));
                ban.setUserId(resultSet.getInt("user_id"));
                ban.setCommunityId((Integer) resultSet.getObject("community_id"));
                ban.setBannedBy(resultSet.getInt("banned_by"));
                ban.setReason(resultSet.getString("reason"));
                ban.setGlobal(resultSet.getBoolean("is_global"));
                ban.setExpiresAt(resultSet.getTimestamp("expires_at"));
                ban.setCreatedAt(resultSet.getTimestamp("created_at"));
                ban.setUsername(resultSet.getString("username"));
                ban.setBannedByUsername(resultSet.getString("banned_by_username"));
                ban.setCommunityName(resultSet.getString("community_name"));
                bans.add(ban);
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load bans.", exception);
        }

        return bans;
    }
}
