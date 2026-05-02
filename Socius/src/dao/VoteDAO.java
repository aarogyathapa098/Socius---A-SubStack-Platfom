package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Vote;

public class VoteDAO {

    public Vote getVote(int userId, int postId) {
        String sql =
            "SELECT vote_id, user_id, post_id, vote_type, created_at "
                + "FROM votes WHERE user_id = ? AND post_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);
            statement.setInt(2, postId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapVote(resultSet);
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load vote.", exception);
        }

        return null;
    }

    public void insertVote(int userId, int postId, String voteType) {
        String sql = "INSERT INTO votes (user_id, post_id, vote_type) VALUES (?, ?, ?)";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);
            statement.setInt(2, postId);
            statement.setString(3, voteType);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to insert vote.", exception);
        }
    }

    public void updateVote(int userId, int postId, String voteType) {
        String sql = "UPDATE votes SET vote_type = ? WHERE user_id = ? AND post_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, voteType);
            statement.setInt(2, userId);
            statement.setInt(3, postId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to update vote.", exception);
        }
    }

    public void deleteVote(int userId, int postId) {
        String sql = "DELETE FROM votes WHERE user_id = ? AND post_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);
            statement.setInt(2, postId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to delete vote.", exception);
        }
    }

    public void recalculateVotes(int postId) {
        String countSql =
            "SELECT "
                + "SUM(CASE WHEN vote_type = 'up' THEN 1 ELSE 0 END) AS upvote_count, "
                + "SUM(CASE WHEN vote_type = 'down' THEN 1 ELSE 0 END) AS downvote_count "
                + "FROM votes WHERE post_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(countSql)
        ) {
            statement.setInt(1, postId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    new PostDAO().updateVoteCount(
                        postId,
                        resultSet.getInt("upvote_count"),
                        resultSet.getInt("downvote_count")
                    );
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to recalculate votes.", exception);
        }
    }

    private Vote mapVote(ResultSet resultSet) throws SQLException {
        Vote vote = new Vote();
        vote.setVoteId(resultSet.getInt("vote_id"));
        vote.setUserId(resultSet.getInt("user_id"));
        vote.setPostId(resultSet.getInt("post_id"));
        vote.setVoteType(resultSet.getString("vote_type"));
        vote.setCreatedAt(resultSet.getTimestamp("created_at"));
        return vote;
    }
}
