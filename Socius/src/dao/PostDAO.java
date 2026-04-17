package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Post;

public class PostDAO {

    private static final String POST_SELECT =
        "SELECT p.post_id, p.community_id, p.author_id, p.title, p.content, p.post_type, p.resource_url, "
            + "p.status, p.is_featured, p.upvotes, p.downvotes, p.comment_count, "
            + "COALESCE((SELECT COUNT(*) FROM comments cm WHERE cm.post_id = p.post_id AND cm.is_removed = 0), p.comment_count) "
            + "AS live_comment_count, "
            + "p.view_count, p.rejection_reason, p.reviewed_by, p.reviewed_at, p.created_at, p.updated_at, "
            + "u.username AS author_username, c.name AS community_name, c.slug AS community_slug "
            + "FROM posts p "
            + "JOIN users u ON p.author_id = u.user_id "
            + "JOIN communities c ON p.community_id = c.community_id ";

    public int insertPost(Post post) {
        String sql =
            "INSERT INTO posts (community_id, author_id, title, content, post_type, resource_url, status, is_featured) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setInt(1, post.getCommunityId());
            statement.setInt(2, post.getAuthorId());
            statement.setString(3, post.getTitle());
            statement.setString(4, post.getContent());
            statement.setString(5, post.getPostType() != null ? post.getPostType() : "text");
            statement.setString(6, post.getResourceUrl());
            statement.setString(7, post.getStatus() != null ? post.getStatus() : "pending");
            statement.setBoolean(8, post.isFeatured());
            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to create post.", exception);
        }

        return 0;
    }

    public Post getPostById(int postId) {
        String sql = POST_SELECT + "WHERE p.post_id = ? AND p.status = 'approved'";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, postId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapPost(resultSet);
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load post.", exception);
        }

        return null;
    }

    public List<Post> getApprovedPostsByCommunity(int communityId, int limit, int offset) {
        List<Post> posts = new ArrayList<Post>();
        String sql =
            POST_SELECT
                + "WHERE p.community_id = ? AND p.status = 'approved' "
                + "ORDER BY p.is_featured DESC, p.created_at DESC LIMIT ? OFFSET ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, communityId);
            statement.setInt(2, limit);
            statement.setInt(3, offset);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    posts.add(mapPost(resultSet));
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load community posts.", exception);
        }

        return posts;
    }

    public List<Post> getPendingPostsByCommunity(int communityId) {
        List<Post> posts = new ArrayList<Post>();
        String sql =
            POST_SELECT
                + "WHERE p.community_id = ? AND p.status = 'pending' "
                + "ORDER BY p.created_at DESC";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, communityId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    posts.add(mapPost(resultSet));
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load pending community posts.", exception);
        }

        return posts;
    }

    public List<Post> getAllPendingPosts() {
        List<Post> posts = new ArrayList<Post>();
        String sql =
            POST_SELECT
                + "WHERE p.status = 'pending' "
                + "ORDER BY p.created_at ASC";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                posts.add(mapPost(resultSet));
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load pending posts.", exception);
        }

        return posts;
    }

    public List<Post> getPostsByAuthor(int authorId) {
        List<Post> posts = new ArrayList<Post>();
        String sql = POST_SELECT + "WHERE p.author_id = ? ORDER BY p.updated_at DESC";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, authorId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    posts.add(mapPost(resultSet));
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load author posts.", exception);
        }

        return posts;
    }

    public List<Post> getTrendingPosts(int limit) {
        List<Post> posts = new ArrayList<Post>();
        String sql =
            POST_SELECT
                + "WHERE p.status = 'approved' "
                + "ORDER BY p.is_featured DESC, p.upvotes DESC, p.created_at DESC LIMIT ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    posts.add(mapPost(resultSet));
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load trending posts.", exception);
        }

        return posts;
    }

    public List<Post> getRecentPosts(int limit) {
        List<Post> posts = new ArrayList<Post>();
        String sql = POST_SELECT + "ORDER BY p.created_at DESC LIMIT ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    posts.add(mapPost(resultSet));
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load recent posts.", exception);
        }

        return posts;
    }

    public List<Post> getRecentApprovedPosts(int limit) {
        List<Post> posts = new ArrayList<Post>();
        String sql =
            POST_SELECT
                + "WHERE p.status = 'approved' "
                + "ORDER BY p.created_at DESC LIMIT ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    posts.add(mapPost(resultSet));
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load recent approved posts.", exception);
        }

        return posts;
    }

    public void updateStatus(int postId, String status, Integer reviewedBy, String reason) {
        String sql =
            "UPDATE posts SET status = ?, reviewed_by = ?, reviewed_at = CURRENT_TIMESTAMP, rejection_reason = ? "
                + "WHERE post_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, status);
            if (reviewedBy != null) {
                statement.setInt(2, reviewedBy.intValue());
            } else {
                statement.setNull(2, java.sql.Types.INTEGER);
            }
            statement.setString(3, reason);
            statement.setInt(4, postId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to update post status.", exception);
        }
    }

    public void updateVoteCount(int postId, int upvotes, int downvotes) {
    }

    public void incrementViewCount(int postId) {
    }

    public void setFeatured(int postId, boolean featured) {
    }

    public void deletePost(int postId) {
    }

    public int getApprovedPostCount() {
        return getPostCountByStatus("approved");
    }

    public int getPendingPostCount() {
        return getPostCountByStatus("pending");
    }

    public int getApprovedPostCountByCommunity(int communityId) {
        String sql = "SELECT COUNT(*) FROM posts WHERE community_id = ? AND status = 'approved'";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, communityId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to count approved posts for community.", exception);
        }

        return 0;
    }

    private int getPostCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM posts WHERE status = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, status);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to count posts.", exception);
        }

        return 0;
    }

    private Post mapPost(ResultSet resultSet) throws SQLException {
        Post post = new Post();
        post.setPostId(resultSet.getInt("post_id"));
        post.setCommunityId(resultSet.getInt("community_id"));
        post.setAuthorId(resultSet.getInt("author_id"));
        post.setTitle(resultSet.getString("title"));
        post.setContent(resultSet.getString("content"));
        post.setPostType(resultSet.getString("post_type"));
        post.setResourceUrl(resultSet.getString("resource_url"));
        post.setStatus(resultSet.getString("status"));
        post.setFeatured(resultSet.getBoolean("is_featured"));
        post.setUpvotes(resultSet.getInt("upvotes"));
        post.setDownvotes(resultSet.getInt("downvotes"));
        post.setCommentCount(readInt(resultSet, "live_comment_count", "comment_count"));
        post.setViewCount(resultSet.getInt("view_count"));
        post.setRejectionReason(resultSet.getString("rejection_reason"));
        post.setReviewedBy((Integer) resultSet.getObject("reviewed_by"));
        post.setReviewedAt(resultSet.getTimestamp("reviewed_at"));
        post.setCreatedAt(resultSet.getTimestamp("created_at"));
        post.setUpdatedAt(resultSet.getTimestamp("updated_at"));
        post.setAuthorUsername(resultSet.getString("author_username"));
        post.setCommunityName(resultSet.getString("community_name"));
        post.setCommunitySlug(resultSet.getString("community_slug"));
        return post;
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
