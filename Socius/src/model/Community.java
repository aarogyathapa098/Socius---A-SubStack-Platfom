package model;

import java.sql.Timestamp;

public class Community {
    private int communityId;
    private String name;
    private String slug;
    private String description;
    private String guidelines;
    private String bannerStyle;
    private String iconName;
    private boolean privateCommunity;
    private boolean requiresReview;
    private int memberCount;
    private int createdBy;
    private Timestamp createdAt;

    public Community() {
    }

    public Community(
        int communityId,
        String name,
        String slug,
        String description,
        String guidelines,
        String bannerStyle,
        String iconName,
        boolean privateCommunity,
        boolean requiresReview,
        int memberCount,
        int createdBy,
        Timestamp createdAt
    ) {
        this.communityId = communityId;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.guidelines = guidelines;
        this.bannerStyle = bannerStyle;
        this.iconName = iconName;
        this.privateCommunity = privateCommunity;
        this.requiresReview = requiresReview;
        this.memberCount = memberCount;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public int getCommunityId() {
        return communityId;
    }

    public void setCommunityId(int communityId) {
        this.communityId = communityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGuidelines() {
        return guidelines;
    }

    public void setGuidelines(String guidelines) {
        this.guidelines = guidelines;
    }

    public String getBannerStyle() {
        return bannerStyle;
    }

    public void setBannerStyle(String bannerStyle) {
        this.bannerStyle = bannerStyle;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public boolean isPrivateCommunity() {
        return privateCommunity;
    }

    public void setPrivateCommunity(boolean privateCommunity) {
        this.privateCommunity = privateCommunity;
    }

    public boolean isRequiresReview() {
        return requiresReview;
    }

    public void setRequiresReview(boolean requiresReview) {
        this.requiresReview = requiresReview;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
