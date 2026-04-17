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
    }

    public List<Report> getOpenReportsByCommunity(int communityId) {
        return new ArrayList<Report>();
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

    public void updateReportStatus(int reportId, String status, Integer reviewedBy) {
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
