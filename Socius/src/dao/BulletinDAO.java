package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Bulletin;

public class BulletinDAO {

    public void insertBulletin(Bulletin bulletin) {
        String sql =
            "INSERT INTO bulletins (community_id, sent_by, subject, body, recipient_count) VALUES (?, ?, ?, ?, ?)";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, bulletin.getCommunityId());
            statement.setInt(2, bulletin.getSentBy());
            statement.setString(3, bulletin.getSubject());
            statement.setString(4, bulletin.getBody());
            statement.setInt(5, bulletin.getRecipientCount());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to send bulletin.", exception);
        }
    }

    public List<Bulletin> getBulletinsByCommunity(int communityId) {
        return new ArrayList<Bulletin>();
    }

    public List<Bulletin> getRecentBulletins(int limit) {
        List<Bulletin> bulletins = new ArrayList<Bulletin>();
        String sql =
            "SELECT b.*, c.name AS community_name, u.username AS sent_by_username "
                + "FROM bulletins b "
                + "JOIN communities c ON b.community_id = c.community_id "
                + "JOIN users u ON b.sent_by = u.user_id "
                + "ORDER BY b.sent_at DESC LIMIT ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Bulletin bulletin = new Bulletin();
                    bulletin.setBulletinId(resultSet.getInt("bulletin_id"));
                    bulletin.setCommunityId(resultSet.getInt("community_id"));
                    bulletin.setSentBy(resultSet.getInt("sent_by"));
                    bulletin.setSubject(resultSet.getString("subject"));
                    bulletin.setBody(resultSet.getString("body"));
                    bulletin.setRecipientCount(resultSet.getInt("recipient_count"));
                    bulletin.setSentAt(resultSet.getTimestamp("sent_at"));
                    bulletin.setCommunityName(resultSet.getString("community_name"));
                    bulletin.setSentByUsername(resultSet.getString("sent_by_username"));
                    bulletins.add(bulletin);
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load recent bulletins.", exception);
        }

        return bulletins;
    }

    public int getTotalBulletinCount() {
        String sql = "SELECT COUNT(*) FROM bulletins";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to count bulletins.", exception);
        }

        return 0;
    }
}
