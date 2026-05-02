package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.Comment;

public class CommentDAO {

    public int insertComment(Comment comment) {
        String sql =
            "INSERT INTO comments (post_id, author_id, parent_id, content) "
                + "VALUES (?, ?, ?, ?)";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setInt(1, comment.getPostId());
            statement.setInt(2, comment.getAuthorId());
            if (comment.getParentId() != null) {
                statement.setInt(3, comment.getParentId().intValue());
            } else {
                statement.setNull(3, java.sql.Types.INTEGER);
            }
            statement.setString(4, comment.getContent());
            statement.executeUpdate();
            incrementCommentCount(comment.getPostId());

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to insert comment.", exception);
        }

        return 0;
    }

    public List<Comment> getCommentsByPost(int postId) {
        List<Comment> allComments = new ArrayList<Comment>();
        Map<Integer, Comment> commentsById = new LinkedHashMap<Integer, Comment>();
        List<Comment> rootComments = new ArrayList<Comment>();
        String sql =
            "SELECT c.comment_id, c.post_id, c.author_id, c.parent_id, c.content, c.upvotes, c.is_removed, c.created_at, "
                + "u.username AS author_username "
                + "FROM comments c "
                + "JOIN users u ON c.author_id = u.user_id "
                + "WHERE c.post_id = ? "
                + "ORDER BY c.created_at ASC";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, postId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Comment comment = mapComment(resultSet);
                    allComments.add(comment);
                    commentsById.put(Integer.valueOf(comment.getCommentId()), comment);
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load comments.", exception);
        }

        for (Comment comment : allComments) {
            if (comment.getParentId() != null && commentsById.containsKey(comment.getParentId())) {
                commentsById.get(comment.getParentId()).getReplies().add(comment);
            } else {
                rootComments.add(comment);
            }
        }

        return rootComments;
    }

    public void removeComment(int commentId) {
        String sql = "UPDATE comments SET is_removed = 1 WHERE comment_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, commentId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to remove comment.", exception);
        }
    }

    public void incrementCommentCount(int postId) {
        String sql =
            "UPDATE posts SET comment_count = ("
                + "SELECT COUNT(*) FROM comments WHERE post_id = ? AND is_removed = 0"
                + ") WHERE post_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, postId);
            statement.setInt(2, postId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to refresh post comment count.", exception);
        }
    }

    private Comment mapComment(ResultSet resultSet) throws SQLException {
        Comment comment = new Comment();
        comment.setCommentId(resultSet.getInt("comment_id"));
        comment.setPostId(resultSet.getInt("post_id"));
        comment.setAuthorId(resultSet.getInt("author_id"));
        comment.setParentId((Integer) resultSet.getObject("parent_id"));
        comment.setContent(resultSet.getString("content"));
        comment.setUpvotes(resultSet.getInt("upvotes"));
        comment.setRemoved(resultSet.getBoolean("is_removed"));
        comment.setCreatedAt(resultSet.getTimestamp("created_at"));
        comment.setAuthorUsername(resultSet.getString("author_username"));
        return comment;
    }
}
