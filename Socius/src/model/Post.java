package model;

import java.sql.Timestamp;

public class Post {
    private int postId;
    private int communityId;
    private int authorId;
    private String title;
    private String content;
    private String postType;
    private String resourceUrl;
    private String imageUrl;
    private String imageAltText;
    private String status;
    private boolean featured;
    private int upvotes;
    private int downvotes;
    private int commentCount;
    private int viewCount;
    private String rejectionReason;
    private Integer reviewedBy;
    private Timestamp reviewedAt;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String authorUsername;
    private String communityName;
    private String communitySlug;

    public Post() {
    }

    public Post(
        int postId,
        int communityId,
        int authorId,
        String title,
        String content,
        String postType,
        String resourceUrl,
        String imageUrl,
        String imageAltText,
        String status,
        boolean featured,
        int upvotes,
        int downvotes,
        int commentCount,
        int viewCount,
        String rejectionReason,
        Integer reviewedBy,
        Timestamp reviewedAt,
        Timestamp createdAt,
        Timestamp updatedAt,
        String authorUsername,
        String communityName,
        String communitySlug
    ) {
        this.postId = postId;
        this.communityId = communityId;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.postType = postType;
        this.resourceUrl = resourceUrl;
        this.imageUrl = imageUrl;
        this.imageAltText = imageAltText;
        this.status = status;
        this.featured = featured;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.commentCount = commentCount;
        this.viewCount = viewCount;
        this.rejectionReason = rejectionReason;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = reviewedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.authorUsername = authorUsername;
        this.communityName = communityName;
        this.communitySlug = communitySlug;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getCommunityId() {
        return communityId;
    }

    public void setCommunityId(int communityId) {
        this.communityId = communityId;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageAltText() {
        return imageAltText;
    }

    public void setImageAltText(String imageAltText) {
        this.imageAltText = imageAltText;
    }

    public boolean hasImage() {
        return imageUrl != null && !imageUrl.trim().isEmpty();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Integer getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(Integer reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public Timestamp getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(Timestamp reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getCommunitySlug() {
        return communitySlug;
    }

    public void setCommunitySlug(String communitySlug) {
        this.communitySlug = communitySlug;
    }
}
