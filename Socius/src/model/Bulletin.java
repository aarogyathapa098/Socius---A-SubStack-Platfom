package model;

import java.sql.Timestamp;

public class Bulletin {
    private int bulletinId;
    private int communityId;
    private int sentBy;
    private String subject;
    private String body;
    private int recipientCount;
    private Timestamp sentAt;
    private String communityName;
    private String sentByUsername;

    public Bulletin() {
    }

    public Bulletin(
        int bulletinId,
        int communityId,
        int sentBy,
        String subject,
        String body,
        int recipientCount,
        Timestamp sentAt
    ) {
        this.bulletinId = bulletinId;
        this.communityId = communityId;
        this.sentBy = sentBy;
        this.subject = subject;
        this.body = body;
        this.recipientCount = recipientCount;
        this.sentAt = sentAt;
    }

    public int getBulletinId() {
        return bulletinId;
    }

    public void setBulletinId(int bulletinId) {
        this.bulletinId = bulletinId;
    }

    public int getCommunityId() {
        return communityId;
    }

    public void setCommunityId(int communityId) {
        this.communityId = communityId;
    }

    public int getSentBy() {
        return sentBy;
    }

    public void setSentBy(int sentBy) {
        this.sentBy = sentBy;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getRecipientCount() {
        return recipientCount;
    }

    public void setRecipientCount(int recipientCount) {
        this.recipientCount = recipientCount;
    }

    public Timestamp getSentAt() {
        return sentAt;
    }

    public void setSentAt(Timestamp sentAt) {
        this.sentAt = sentAt;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getSentByUsername() {
        return sentByUsername;
    }

    public void setSentByUsername(String sentByUsername) {
        this.sentByUsername = sentByUsername;
    }
}
