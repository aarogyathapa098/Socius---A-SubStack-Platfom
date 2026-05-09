<%@ page import="model.Post" %>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>
<%
Post editPost = (Post) request.getAttribute("editPost");
String postError = (String) request.getAttribute("postError");
String submittedTitle = (String) request.getAttribute("submittedTitle");
String submittedPostType = (String) request.getAttribute("submittedPostType");
String submittedContent = (String) request.getAttribute("submittedContent");
String submittedImageAltText = (String) request.getAttribute("submittedImageAltText");

String title = submittedTitle != null ? submittedTitle : (editPost != null ? editPost.getTitle() : "");
String postType = submittedPostType != null ? submittedPostType : (editPost != null ? editPost.getPostType() : "text");
String content = submittedContent != null ? submittedContent : (editPost != null ? editPost.getContent() : "");
String imageAltText = submittedImageAltText != null ? submittedImageAltText : (editPost != null && editPost.getImageAltText() != null ? editPost.getImageAltText() : "");
%>
<main class="content-shell">
    <section class="section-card">
        <p class="eyebrow">Edit Post</p>
        <h1>Revise your contribution.</h1>
        <p class="lead-sm">Change the text, add an image, replace the current image, or remove it before sending the post back for review.</p>
    </section>

    <% if (postError != null) { %>
        <div class="flash flash--warning"><%= postError %></div>
    <% } %>
    <div id="postImageSizeWarning" class="flash flash--warning" hidden></div>

    <% if (editPost != null) { %>
        <section class="split-section">
            <div class="section-card">
                <form class="stack-form" action="${pageContext.request.contextPath}/member/edit-post" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="postId" value="<%= editPost.getPostId() %>">

                    <label for="editTitle">Title</label>
                    <input id="editTitle" name="title" type="text" value="<%= title %>" required>

                    <label for="editPostType">Post type</label>
                    <select id="editPostType" name="postType">
                        <option value="text" <%= postType == null || "text".equalsIgnoreCase(postType) ? "selected" : "" %>>Text</option>
                        <option value="resource" <%= "resource".equalsIgnoreCase(postType) ? "selected" : "" %>>Resource</option>
                        <option value="event" <%= "event".equalsIgnoreCase(postType) ? "selected" : "" %>>Event</option>
                        <option value="image" <%= "image".equalsIgnoreCase(postType) ? "selected" : "" %>>Image</option>
                    </select>

                    <label for="editBody">Content</label>
                    <textarea id="editBody" name="content" rows="10"><%= content != null ? content : "" %></textarea>

                    <label for="postImage">Add or replace image</label>
                    <input id="postImage" name="postImage" type="file" accept="image/jpeg,image/png,image/gif,image/webp">
                    <p class="form-hint">Upload one JPG, PNG, GIF, or WebP image up to 5 MB.</p>

                    <label for="imageAltText">Image description</label>
                    <input
                        id="imageAltText"
                        name="imageAltText"
                        type="text"
                        placeholder="Describe the image for accessibility"
                        value="<%= imageAltText %>"
                    >

                    <% if (editPost.hasImage()) { %>
                        <label class="checkbox-row">
                            <input name="removeImage" type="checkbox">
                            <span>Remove current image</span>
                        </label>
                    <% } %>

                    <div class="form-actions">
                        <button class="button button--ghost" type="submit" name="submissionAction" value="save-draft">Save draft</button>
                        <button class="button button--primary" type="submit" name="submissionAction" value="submit-review">Submit for review</button>
                    </div>
                </form>
            </div>

            <div class="section-card">
                <p class="eyebrow">Current Post</p>
                <h2><%= editPost.getStatus() %></h2>
                <% if (editPost.hasImage()) { %>
                    <figure class="post-media">
                        <img
                            src="${pageContext.request.contextPath}<%= editPost.getImageUrl() %>"
                            alt="<%= editPost.getImageAltText() != null ? editPost.getImageAltText() : editPost.getTitle() %>"
                            width="1200"
                            height="675"
                        >
                    </figure>
                <% } else { %>
                    <p class="muted">This post does not have an image yet.</p>
                <% } %>

                <% if (editPost.getRejectionReason() != null && !editPost.getRejectionReason().trim().isEmpty()) { %>
                    <p class="eyebrow">Moderator feedback</p>
                    <p class="muted"><%= editPost.getRejectionReason() %></p>
                <% } %>
            </div>
        </section>
    <% } %>
</main>
<%@ include file="../common/footer.jsp" %>
