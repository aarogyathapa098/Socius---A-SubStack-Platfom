package model;

import java.sql.Timestamp;

public class Ban {
    private int banId;
    private int userId;
    private Integer communityId;
    private int bannedBy;
    private String reason;
    private boolean global;
    private Timestamp expiresAt;
    private Timestamp createdAt;
    private String username;
    private String bannedByUsername;
    private String communityName;

    public Ban() {
    }

    public Ban(
        int banId,
        int userId,
        Integer communityId,
        int bannedBy,
        String reason,
        boolean global,
        Timestamp expiresAt,
        Timestamp createdAt,
        String username,
        String bannedByUsername
    ) {
        this.banId = banId;
        this.userId = userId;
        this.communityId = communityId;
        this.bannedBy = bannedBy;
        this.reason = reason;
        this.global = global;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.username = username;
        this.bannedByUsername = bannedByUsername;
    }

    public int getBanId() {
        return banId;
    }

    public void setBanId(int banId) {
        this.banId = banId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Integer communityId) {
        this.communityId = communityId;
    }

    public int getBannedBy() {
        return bannedBy;
    }

    public void setBannedBy(int bannedBy) {
        this.bannedBy = bannedBy;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public Timestamp getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Timestamp expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBannedByUsername() {
        return bannedByUsername;
    }

    public void setBannedByUsername(String bannedByUsername) {
        this.bannedByUsername = bannedByUsername;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }
}
