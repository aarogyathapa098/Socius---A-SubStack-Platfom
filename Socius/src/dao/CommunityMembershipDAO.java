package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Community;
import model.User;

public class CommunityMembershipDAO {

    public boolean isMember(int userId, int communityId) {
        String sql = "SELECT 1 FROM community_memberships WHERE user_id = ? AND community_id = ?";

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
            throw new RuntimeException("Failed to check community membership.", exception);
        }
    }

    public void joinCommunity(int userId, int communityId) {
        String insertSql =
            "INSERT INTO community_memberships (community_id, user_id) VALUES (?, ?)";
        String updateCountSql =
            "UPDATE communities SET member_count = (SELECT COUNT(*) FROM community_memberships WHERE community_id = ?) WHERE community_id = ?";

        try (
            Connection connection = DBConnection.getConnection()
        ) {
            connection.setAutoCommit(false);

            try (
                PreparedStatement insertStatement = connection.prepareStatement(insertSql);
                PreparedStatement updateStatement = connection.prepareStatement(updateCountSql)
            ) {
                insertStatement.setInt(1, communityId);
                insertStatement.setInt(2, userId);
                insertStatement.executeUpdate();

                updateStatement.setInt(1, communityId);
                updateStatement.setInt(2, communityId);
                updateStatement.executeUpdate();

                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to join community.", exception);
        }
    }

    public void leaveCommunity(int userId, int communityId) {
        String deleteSql =
            "DELETE FROM community_memberships WHERE user_id = ? AND community_id = ?";
        String updateCountSql =
            "UPDATE communities SET member_count = (SELECT COUNT(*) FROM community_memberships WHERE community_id = ?) WHERE community_id = ?";

        try (
            Connection connection = DBConnection.getConnection()
        ) {
            connection.setAutoCommit(false);

            try (
                PreparedStatement deleteStatement = connection.prepareStatement(deleteSql);
                PreparedStatement updateStatement = connection.prepareStatement(updateCountSql)
            ) {
                deleteStatement.setInt(1, userId);
                deleteStatement.setInt(2, communityId);
                deleteStatement.executeUpdate();

                updateStatement.setInt(1, communityId);
                updateStatement.setInt(2, communityId);
                updateStatement.executeUpdate();

                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to leave community.", exception);
        }
    }

    public List<Community> getCommunitiesForUser(int userId) {
        List<Community> communities = new ArrayList<Community>();
        String sql =
            "SELECT c.community_id, c.name, c.slug, c.description, c.guidelines, c.banner_style, c.icon_name, "
                + "COALESCE((SELECT COUNT(*) FROM community_memberships inner_cm WHERE inner_cm.community_id = c.community_id), c.member_count) AS live_member_count, "
                + "c.is_private, c.requires_review, c.approval_status, c.member_count, c.created_by, c.created_at "
                + "FROM community_memberships cm "
                + "JOIN communities c ON cm.community_id = c.community_id "
                + "WHERE cm.user_id = ? ORDER BY cm.joined_at DESC";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Community community = new Community();
                    community.setCommunityId(resultSet.getInt("community_id"));
                    community.setName(resultSet.getString("name"));
                    community.setSlug(resultSet.getString("slug"));
                    community.setDescription(resultSet.getString("description"));
                    community.setGuidelines(resultSet.getString("guidelines"));
                    community.setBannerStyle(resultSet.getString("banner_style"));
                    community.setIconName(resultSet.getString("icon_name"));
                    community.setPrivateCommunity(resultSet.getBoolean("is_private"));
                    community.setRequiresReview(resultSet.getBoolean("requires_review"));
                    community.setApprovalStatus(resultSet.getString("approval_status"));
                    community.setMemberCount(resultSet.getInt("live_member_count"));
                    community.setCreatedBy(resultSet.getInt("created_by"));
                    community.setCreatedAt(resultSet.getTimestamp("created_at"));
                    communities.add(community);
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load communities for user.", exception);
        }

        return communities;
    }

    public List<User> getMembersOfCommunity(int communityId) {
        return new ArrayList<User>();
    }
}
