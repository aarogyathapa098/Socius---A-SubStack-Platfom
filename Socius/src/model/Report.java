package model;

import java.sql.Timestamp;

public class Report {
    private int reportId;
    private int reporterId;
    private Integer postId;
    private Integer commentId;
    private String reason;
    private String status;
    private Integer reviewedBy;
    private Timestamp createdAt;
    private String reporterUsername;
    private String postTitle;
    private String commentContent;

    public Report() {
    }

    public Report(
        int reportId,
        int reporterId,
        Integer postId,
        Integer commentId,
        String reason,
        String status,
        Integer reviewedBy,
        Timestamp createdAt
    ) {
        this.reportId = reportId;
        this.reporterId = reporterId;
        this.postId = postId;
        this.commentId = commentId;
        this.reason = reason;
        this.status = status;
        this.reviewedBy = reviewedBy;
        this.createdAt = createdAt;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getReporterId() {
        return reporterId;
    }

    public void setReporterId(int reporterId) {
        this.reporterId = reporterId;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(Integer reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getReporterUsername() {
        return reporterUsername;
    }

    public void setReporterUsername(String reporterUsername) {
        this.reporterUsername = reporterUsername;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
}
