package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.CommunityModerator;

public class CommunityModeratorDAO {

    public List<CommunityModerator> getAllModerators() {
        List<CommunityModerator> moderators = new ArrayList<CommunityModerator>();
        String sql =
            "SELECT cm.moderator_id, cm.community_id, cm.user_id, cm.assigned_by, cm.assigned_at, "
                + "u.username, c.name AS community_name "
                + "FROM community_moderators cm "
                + "JOIN users u ON cm.user_id = u.user_id "
                + "JOIN communities c ON cm.community_id = c.community_id "
                + "ORDER BY cm.assigned_at DESC";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                CommunityModerator moderator = new CommunityModerator();
                moderator.setModeratorId(resultSet.getInt("moderator_id"));
                moderator.setCommunityId(resultSet.getInt("community_id"));
                moderator.setUserId(resultSet.getInt("user_id"));
                moderator.setAssignedBy(resultSet.getInt("assigned_by"));
                moderator.setAssignedAt(resultSet.getTimestamp("assigned_at"));
                moderator.setUsername(resultSet.getString("username"));
                moderator.setCommunityName(resultSet.getString("community_name"));
                moderators.add(moderator);
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load community moderators.", exception);
        }

        return moderators;
    }

    public boolean isModeratorAssigned(int userId, int communityId) {
        String sql = "SELECT 1 FROM community_moderators WHERE user_id = ? AND community_id = ?";

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
            throw new RuntimeException("Failed to check moderator assignment.", exception);
        }
    }

    public void assignModerator(int userId, int communityId, int assignedBy) {
        String sql =
            "INSERT INTO community_moderators (community_id, user_id, assigned_by) VALUES (?, ?, ?)";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, communityId);
            statement.setInt(2, userId);
            statement.setInt(3, assignedBy);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to assign moderator.", exception);
        }
    }

    public Integer getUserIdForModeratorAssignment(int moderatorId) {
        String sql = "SELECT user_id FROM community_moderators WHERE moderator_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, moderatorId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Integer.valueOf(resultSet.getInt("user_id"));
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load moderator assignment.", exception);
        }

        return null;
    }

    public int countAssignmentsForUser(int userId) {
        String sql = "SELECT COUNT(*) FROM community_moderators WHERE user_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to count moderator assignments.", exception);
        }

        return 0;
    }

    public void removeModerator(int moderatorId) {
        String sql = "DELETE FROM community_moderators WHERE moderator_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, moderatorId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to remove moderator assignment.", exception);
        }
    }
}
