package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.Comment;

public class CommentDAO {

    public int insertComment(Comment comment) {
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
    }

    public void incrementCommentCount(int postId) {
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
