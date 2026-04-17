package model;

import java.sql.Timestamp;

public class User {
    private int userId;
    private String username;
    private String email;
    private String phoneNumber;
    private String passwordHash;
    private String displayName;
    private String bio;
    private String avatarUrl;
    private String role;
    private int penaltyPoints;
    private int warningCount;
    private boolean active;
    private boolean globallyBanned;
    private int failedAttempts;
    private Timestamp lockedUntil;
    private String resetToken;
    private Timestamp resetTokenExpiresAt;
    private Timestamp createdAt;

    public User() {
    }

    public User(
        int userId,
        String username,
        String email,
        String phoneNumber,
        String passwordHash,
        String displayName,
        String bio,
        String avatarUrl,
        String role,
        int penaltyPoints,
        int warningCount,
        boolean active,
        boolean globallyBanned,
        int failedAttempts,
        Timestamp lockedUntil,
        String resetToken,
        Timestamp resetTokenExpiresAt,
        Timestamp createdAt
    ) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.role = role;
        this.penaltyPoints = penaltyPoints;
        this.warningCount = warningCount;
        this.active = active;
        this.globallyBanned = globallyBanned;
        this.failedAttempts = failedAttempts;
        this.lockedUntil = lockedUntil;
        this.resetToken = resetToken;
        this.resetTokenExpiresAt = resetTokenExpiresAt;
        this.createdAt = createdAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getPenaltyPoints() {
        return penaltyPoints;
    }

    public void setPenaltyPoints(int penaltyPoints) {
        this.penaltyPoints = penaltyPoints;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(int warningCount) {
        this.warningCount = warningCount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isGloballyBanned() {
        return globallyBanned;
    }

    public void setGloballyBanned(boolean globallyBanned) {
        this.globallyBanned = globallyBanned;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public Timestamp getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(Timestamp lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public Timestamp getResetTokenExpiresAt() {
        return resetTokenExpiresAt;
    }

    public void setResetTokenExpiresAt(Timestamp resetTokenExpiresAt) {
        this.resetTokenExpiresAt = resetTokenExpiresAt;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
