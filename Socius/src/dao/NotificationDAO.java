package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Notification;

public class NotificationDAO {

    public void createNotification(int userId, String message, String targetUrl) {
        String sql = "INSERT INTO notifications (user_id, message, target_url) VALUES (?, ?, ?)";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);
            statement.setString(2, message);
            statement.setString(3, targetUrl);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to create notification.", exception);
        }
    }

    public List<Notification> getNotificationsForUser(int userId, int limit) {
        List<Notification> notifications = new ArrayList<Notification>();
        String sql =
            "SELECT notification_id, user_id, message, target_url, is_read, created_at "
                + "FROM notifications WHERE user_id = ? "
                + "ORDER BY created_at DESC LIMIT ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);
            statement.setInt(2, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    notifications.add(mapNotification(resultSet));
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load notifications.", exception);
        }

        return notifications;
    }

    public int getUnreadCount(int userId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = 0";

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
            throw new RuntimeException("Failed to count unread notifications.", exception);
        }

        return 0;
    }

    public void markAllRead(int userId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE user_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to mark notifications read.", exception);
        }
    }

    private Notification mapNotification(ResultSet resultSet) throws SQLException {
        Notification notification = new Notification();
        notification.setNotificationId(resultSet.getInt("notification_id"));
        notification.setUserId(resultSet.getInt("user_id"));
        notification.setMessage(resultSet.getString("message"));
        notification.setTargetUrl(resultSet.getString("target_url"));
        notification.setRead(resultSet.getBoolean("is_read"));
        notification.setCreatedAt(resultSet.getTimestamp("created_at"));
        return notification;
    }
}
