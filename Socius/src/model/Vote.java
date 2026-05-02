package model;

import java.sql.Timestamp;

public class Vote {
    private int voteId;
    private int userId;
    private int postId;
    private String voteType;
    private Timestamp createdAt;

    public Vote() {
    }

    public Vote(int voteId, int userId, int postId, String voteType, Timestamp createdAt) {
        this.voteId = voteId;
        this.userId = userId;
        this.postId = postId;
        this.voteType = voteType;
        this.createdAt = createdAt;
    }

    public int getVoteId() {
        return voteId;
    }

    public void setVoteId(int voteId) {
        this.voteId = voteId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getVoteType() {
        return voteType;
    }

    public void setVoteType(String voteType) {
        this.voteType = voteType;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
