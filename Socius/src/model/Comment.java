package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Comment {
    private int commentId;
    private int postId;
    private int authorId;
    private Integer parentId;
    private String content;
    private int upvotes;
    private boolean removed;
    private Timestamp createdAt;
    private String authorUsername;
    private List<Comment> replies = new ArrayList<Comment>();

    public Comment() {
    }

    public Comment(
        int commentId,
        int postId,
        int authorId,
        Integer parentId,
        String content,
        int upvotes,
        boolean removed,
        Timestamp createdAt,
        String authorUsername,
        List<Comment> replies
    ) {
        this.commentId = commentId;
        this.postId = postId;
        this.authorId = authorId;
        this.parentId = parentId;
        this.content = content;
        this.upvotes = upvotes;
        this.removed = removed;
        this.createdAt = createdAt;
        this.authorUsername = authorUsername;
        this.replies = replies;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public List<Comment> getReplies() {
        return replies;
    }

    public void setReplies(List<Comment> replies) {
        this.replies = replies;
    }
}
