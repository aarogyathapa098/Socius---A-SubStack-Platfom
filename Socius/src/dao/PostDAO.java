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
            + "p.image_url, p.image_alt_text, p.status, p.is_featured, p.upvotes, p.downvotes, p.comment_count, "
            + "COALESCE((SELECT COUNT(*) FROM comments cm WHERE cm.post_id = p.post_id AND cm.is_removed = 0), p.comment_count) "
            + "AS live_comment_count, "
            + "p.view_count, p.rejection_reason, p.reviewed_by, p.reviewed_at, p.created_at, p.updated_at, "
            + "u.username AS author_username, c.name AS community_name, c.slug AS community_slug "
            + "FROM posts p "
            + "JOIN users u ON p.author_id = u.user_id "
            + "JOIN communities c ON p.community_id = c.community_id ";
    private static final String APPROVED_PUBLIC_FILTER =
        "p.status = 'approved' AND c.approval_status = 'approved' ";
    private static final String RANKED_FEED_ORDER =
        "ORDER BY p.is_featured DESC, "
            + "((p.upvotes - p.downvotes) * 3 + live_comment_count * 2 + p.view_count * 0.05) DESC, "
            + "p.created_at DESC";

    public int insertPost(Post post) {
        String sql =
            "INSERT INTO posts (community_id, author_id, title, content, post_type, resource_url, image_url, image_alt_text, status, is_featured) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
            statement.setString(7, post.getImageUrl());
            statement.setString(8, post.getImageAltText());
            statement.setString(9, post.getStatus() != null ? post.getStatus() : "pending");
            statement.setBoolean(10, post.isFeatured());
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
        String sql = POST_SELECT + "WHERE p.post_id = ? AND " + APPROVED_PUBLIC_FILTER;

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

    public Post getPostForModeration(int postId) {
        String sql = POST_SELECT + "WHERE p.post_id = ?";

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
            throw new RuntimeException("Failed to load post for moderation.", exception);
        }

        return null;
    }

    public List<Post> getApprovedPostsByCommunity(int communityId, int limit, int offset) {
        List<Post> posts = new ArrayList<Post>();
        String sql =
            POST_SELECT
                + "WHERE p.community_id = ? AND " + APPROVED_PUBLIC_FILTER
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
                + "WHERE " + APPROVED_PUBLIC_FILTER
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
                + "WHERE " + APPROVED_PUBLIC_FILTER
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

    public List<Post> getAllApprovedPosts() {
        List<Post> posts = new ArrayList<Post>();
        String sql =
            POST_SELECT
                + "WHERE " + APPROVED_PUBLIC_FILTER
                + RANKED_FEED_ORDER;

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                posts.add(mapPost(resultSet));
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load approved feed posts.", exception);
        }

        return posts;
    }

    public List<Post> getExploreFeed() {
        return getAllApprovedPosts();
    }

    public List<Post> getPersonalizedFeed(int userId) {
        List<Post> posts = new ArrayList<Post>();
        String sql =
            POST_SELECT
                + "WHERE " + APPROVED_PUBLIC_FILTER
                + "ORDER BY CASE WHEN EXISTS ("
                + "SELECT 1 FROM community_memberships cm "
                + "WHERE cm.community_id = p.community_id AND cm.user_id = ?"
                + ") THEN 0 ELSE 1 END, "
                + "p.is_featured DESC, "
                + "((p.upvotes - p.downvotes) * 3 + live_comment_count * 2 + p.view_count * 0.05) DESC, "
                + "p.created_at DESC";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    posts.add(mapPost(resultSet));
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load personalized feed posts.", exception);
        }

        return posts;
    }

    public List<Post> searchApprovedPosts(String keyword, int limit) {
        List<Post> posts = new ArrayList<Post>();
        String sql =
            POST_SELECT
                + "WHERE " + APPROVED_PUBLIC_FILTER
                + "AND (LOWER(p.title) LIKE ? OR LOWER(p.content) LIKE ? OR LOWER(c.name) LIKE ?) "
                + RANKED_FEED_ORDER + " LIMIT ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            String wildcard = "%" + keyword.toLowerCase() + "%";
            statement.setString(1, wildcard);
            statement.setString(2, wildcard);
            statement.setString(3, wildcard);
            statement.setInt(4, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    posts.add(mapPost(resultSet));
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to search approved posts.", exception);
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
        String sql = "UPDATE posts SET upvotes = ?, downvotes = ? WHERE post_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, upvotes);
            statement.setInt(2, downvotes);
            statement.setInt(3, postId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to update post vote counts.", exception);
        }
    }

    public void incrementViewCount(int postId) {
        String sql = "UPDATE posts SET view_count = view_count + 1 WHERE post_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, postId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to update post view count.", exception);
        }
    }

    public void setFeatured(int postId, boolean featured) {
        String sql = "UPDATE posts SET is_featured = ? WHERE post_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setBoolean(1, featured);
            statement.setInt(2, postId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to update featured post state.", exception);
        }
    }

    public void deletePost(int postId) {
        String sql = "UPDATE posts SET status = 'removed' WHERE post_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, postId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to remove post.", exception);
        }
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
        post.setImageUrl(readString(resultSet, "image_url"));
        post.setImageAltText(readString(resultSet, "image_alt_text"));
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

    private String readString(ResultSet resultSet, String column) throws SQLException {
        try {
            return resultSet.getString(column);
        } catch (SQLException exception) {
            return null;
        }
    }
}
