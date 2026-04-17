package model;

import java.sql.Timestamp;

public class CommunityMembership {
    private int membershipId;
    private int communityId;
    private int userId;
    private Timestamp joinedAt;

    public CommunityMembership() {
    }

    public CommunityMembership(int membershipId, int communityId, int userId, Timestamp joinedAt) {
        this.membershipId = membershipId;
        this.communityId = communityId;
        this.userId = userId;
        this.joinedAt = joinedAt;
    }

    public int getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(int membershipId) {
        this.membershipId = membershipId;
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

    public Timestamp getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Timestamp joinedAt) {
        this.joinedAt = joinedAt;
    }
}
