package model;

import java.sql.Timestamp;

public class CommunityModerator {
    private int moderatorId;
    private int communityId;
    private int userId;
    private int assignedBy;
    private Timestamp assignedAt;
    private String username;
    private String communityName;

    public CommunityModerator() {
    }

    public CommunityModerator(
        int moderatorId,
        int communityId,
        int userId,
        int assignedBy,
        Timestamp assignedAt,
        String username,
        String communityName
    ) {
        this.moderatorId = moderatorId;
        this.communityId = communityId;
        this.userId = userId;
        this.assignedBy = assignedBy;
        this.assignedAt = assignedAt;
        this.username = username;
        this.communityName = communityName;
    }

    public int getModeratorId() {
        return moderatorId;
    }

    public void setModeratorId(int moderatorId) {
        this.moderatorId = moderatorId;
    }

    public int getCommunityId() {
        return communityId;
    }

    public void setCommunityId(int communityId) {
        this.communityId = communityId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(int assignedBy) {
        this.assignedBy = assignedBy;
    }

    public Timestamp getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Timestamp assignedAt) {
        this.assignedAt = assignedAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }
}
