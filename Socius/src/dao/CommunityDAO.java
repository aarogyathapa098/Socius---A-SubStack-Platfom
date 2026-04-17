package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Community;

public class CommunityDAO {

    private static final String COMMUNITY_SELECT =
        "SELECT c.community_id, c.name, c.slug, c.description, c.guidelines, c.banner_style, c.icon_name, "
            + "c.is_private, c.requires_review, c.member_count, c.created_by, c.created_at, "
            + "COALESCE((SELECT COUNT(*) FROM community_memberships cm WHERE cm.community_id = c.community_id), c.member_count) "
            + "AS live_member_count "
            + "FROM communities c ";

    public int insertCommunity(Community community) {
        String sql =
            "INSERT INTO communities (name, slug, description, guidelines, banner_style, icon_name, is_private, requires_review, created_by) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setString(1, community.getName());
            statement.setString(2, community.getSlug());
            statement.setString(3, community.getDescription());
            statement.setString(4, community.getGuidelines());
            statement.setString(5, community.getBannerStyle());
            statement.setString(6, community.getIconName());
            statement.setBoolean(7, community.isPrivateCommunity());
            statement.setBoolean(8, community.isRequiresReview());
            statement.setInt(9, community.getCreatedBy());
            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to create community.", exception);
        }

        return 0;
    }

    public Community getCommunityById(int communityId) {
        String sql = COMMUNITY_SELECT + "WHERE c.community_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, communityId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapCommunity(resultSet);
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load community by id.", exception);
        }

        return null;
    }

    public Community getCommunityBySlug(String slug) {
        String sql = COMMUNITY_SELECT + "WHERE c.slug = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, slug);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapCommunity(resultSet);
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load community by slug.", exception);
        }

        return null;
    }

    public List<Community> getAllCommunities(int limit, int offset) {
        List<Community> communities = new ArrayList<Community>();
        String sql = COMMUNITY_SELECT + "ORDER BY c.created_at DESC LIMIT ? OFFSET ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, limit);
            statement.setInt(2, offset);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    communities.add(mapCommunity(resultSet));
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load communities.", exception);
        }

        return communities;
    }

    public List<Community> getTrendingCommunities(int limit) {
        List<Community> communities = new ArrayList<Community>();
        String sql = COMMUNITY_SELECT + "ORDER BY live_member_count DESC, c.created_at DESC LIMIT ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    communities.add(mapCommunity(resultSet));
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load trending communities.", exception);
        }

        return communities;
    }

    public List<Community> searchCommunities(String keyword) {
        List<Community> communities = new ArrayList<Community>();
        String sql =
            COMMUNITY_SELECT
                + "WHERE LOWER(c.name) LIKE ? OR LOWER(c.description) LIKE ? "
                + "ORDER BY c.name ASC";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            String wildcard = "%" + keyword.toLowerCase() + "%";
            statement.setString(1, wildcard);
            statement.setString(2, wildcard);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    communities.add(mapCommunity(resultSet));
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to search communities.", exception);
        }

        return communities;
    }

    public void updateCommunity(Community community) {
    }

    public void deleteCommunity(int communityId) {
    }

    public void incrementMemberCount(int communityId) {
    }

    public void decrementMemberCount(int communityId) {
    }

    public int getTotalCommunityCount() {
        String sql = "SELECT COUNT(*) FROM communities";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to count communities.", exception);
        }

        return 0;
    }

    private Community mapCommunity(ResultSet resultSet) throws SQLException {
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
        community.setMemberCount(readInt(resultSet, "live_member_count", "member_count"));
        community.setCreatedBy(resultSet.getInt("created_by"));
        community.setCreatedAt(resultSet.getTimestamp("created_at"));
        return community;
    }

    private int readInt(ResultSet resultSet, String preferredColumn, String fallbackColumn)
        throws SQLException {
        try {
            return resultSet.getInt(preferredColumn);
        } catch (SQLException exception) {
            return resultSet.getInt(fallbackColumn);
        }
    }
}
