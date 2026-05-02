package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Report;

public class ReportDAO {

    public void insertReport(Report report) {
        String sql =
            "INSERT INTO reports (reporter_id, post_id, comment_id, reason, status) "
                + "VALUES (?, ?, ?, ?, 'open')";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, report.getReporterId());
            if (report.getPostId() != null) {
                statement.setInt(2, report.getPostId().intValue());
            } else {
                statement.setNull(2, java.sql.Types.INTEGER);
            }
            if (report.getCommentId() != null) {
                statement.setInt(3, report.getCommentId().intValue());
            } else {
                statement.setNull(3, java.sql.Types.INTEGER);
            }
            statement.setString(4, report.getReason());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to insert report.", exception);
        }
    }

    public List<Report> getOpenReportsByCommunity(int communityId) {
        List<Report> reports = new ArrayList<Report>();
        String sql =
            "SELECT r.report_id, r.reporter_id, r.post_id, r.comment_id, r.reason, r.status, r.reviewed_by, r.created_at, "
                + "u.username AS reporter_username, p.title AS post_title, c.content AS comment_content "
                + "FROM reports r "
                + "JOIN users u ON r.reporter_id = u.user_id "
                + "LEFT JOIN posts p ON r.post_id = p.post_id "
                + "LEFT JOIN comments c ON r.comment_id = c.comment_id "
                + "WHERE r.status = 'open' AND (p.community_id = ? OR c.post_id IN (SELECT post_id FROM posts WHERE community_id = ?)) "
                + "ORDER BY r.created_at ASC";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, communityId);
            statement.setInt(2, communityId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    reports.add(mapReport(resultSet));
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load community reports.", exception);
        }

        return reports;
    }

    public List<Report> getAllOpenReports() {
        List<Report> reports = new ArrayList<Report>();
        String sql =
            "SELECT r.report_id, r.reporter_id, r.post_id, r.comment_id, r.reason, r.status, r.reviewed_by, r.created_at, "
                + "u.username AS reporter_username, p.title AS post_title, c.content AS comment_content "
                + "FROM reports r "
                + "JOIN users u ON r.reporter_id = u.user_id "
                + "LEFT JOIN posts p ON r.post_id = p.post_id "
                + "LEFT JOIN comments c ON r.comment_id = c.comment_id "
                + "WHERE r.status = 'open' "
                + "ORDER BY r.created_at ASC";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                reports.add(mapReport(resultSet));
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load open reports.", exception);
        }

        return reports;
    }

    public Report getReportById(int reportId) {
        String sql =
            "SELECT r.report_id, r.reporter_id, r.post_id, r.comment_id, r.reason, r.status, r.reviewed_by, r.created_at, "
                + "u.username AS reporter_username, p.title AS post_title, c.content AS comment_content "
                + "FROM reports r "
                + "JOIN users u ON r.reporter_id = u.user_id "
                + "LEFT JOIN posts p ON r.post_id = p.post_id "
                + "LEFT JOIN comments c ON r.comment_id = c.comment_id "
                + "WHERE r.report_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, reportId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapReport(resultSet);
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load report.", exception);
        }

        return null;
    }

    public void updateReportStatus(int reportId, String status, Integer reviewedBy) {
        String sql = "UPDATE reports SET status = ?, reviewed_by = ? WHERE report_id = ?";

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
            statement.setInt(3, reportId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to update report status.", exception);
        }
    }

    public int getOpenReportCount() {
        String sql = "SELECT COUNT(*) FROM reports WHERE status = 'open'";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to count open reports.", exception);
        }

        return 0;
    }

    private Report mapReport(ResultSet resultSet) throws SQLException {
        Report report = new Report();
        report.setReportId(resultSet.getInt("report_id"));
        report.setReporterId(resultSet.getInt("reporter_id"));
        report.setPostId((Integer) resultSet.getObject("post_id"));
        report.setCommentId((Integer) resultSet.getObject("comment_id"));
        report.setReason(resultSet.getString("reason"));
        report.setStatus(resultSet.getString("status"));
        report.setReviewedBy((Integer) resultSet.getObject("reviewed_by"));
        report.setCreatedAt(resultSet.getTimestamp("created_at"));
        report.setReporterUsername(resultSet.getString("reporter_username"));
        report.setPostTitle(resultSet.getString("post_title"));
        report.setCommentContent(resultSet.getString("comment_content"));
        return report;
    }
}
