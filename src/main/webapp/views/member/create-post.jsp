<%@ page import="java.util.List" %>
<%@ page import="model.Community" %>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>
<%
List<Community> availableCommunities = (List<Community>) request.getAttribute("availableCommunities");
String postError = (String) request.getAttribute("postError");
Integer submittedCommunityId = (Integer) request.getAttribute("submittedCommunityId");
String submittedTitle = (String) request.getAttribute("submittedTitle");
String submittedPostType = (String) request.getAttribute("submittedPostType");
String submittedContent = (String) request.getAttribute("submittedContent");
String submittedImageAltText = (String) request.getAttribute("submittedImageAltText");
%>
<main class="content-shell">
    <section class="section-card">
        <p class="eyebrow">Create Post</p>
        <h1>Submit a thoughtful contribution.</h1>
        <p class="lead-sm">Drafts stay private and submitted posts move into the moderator approval queue automatically.</p>
    </section>

    <% if (postError != null) { %>
        <div class="flash flash--warning"><%= postError %></div>
    <% } %>

    <section class="section-card">
        <form class="stack-form" action="${pageContext.request.contextPath}/member/create-post" method="post" enctype="multipart/form-data">
            <label for="postCommunity">Community</label>
            <select id="postCommunity" name="communityId" required>
                <option value="">Choose a community</option>
                <% if (availableCommunities != null) { %>
                    <% for (Community community : availableCommunities) { %>
                        <option
                            value="<%= community.getCommunityId() %>"
                            <%= submittedCommunityId != null && submittedCommunityId.intValue() == community.getCommunityId() ? "selected" : "" %>
                        >
                            <%= community.getName() %>
                        </option>
                    <% } %>
                <% } %>
            </select>

            <label for="postTitle">Title</label>
            <input
                id="postTitle"
                name="title"
                type="text"
                placeholder="5 to 300 characters"
                value="<%= submittedTitle != null ? submittedTitle : "" %>"
                required
            >

            <label for="postType">Post type</label>
            <select id="postType" name="postType">
                <option value="text" <%= submittedPostType == null || "text".equalsIgnoreCase(submittedPostType) ? "selected" : "" %>>Text</option>
                <option value="resource" <%= "resource".equalsIgnoreCase(submittedPostType) ? "selected" : "" %>>Resource</option>
                <option value="event" <%= "event".equalsIgnoreCase(submittedPostType) ? "selected" : "" %>>Event</option>
                <option value="image" <%= "image".equalsIgnoreCase(submittedPostType) ? "selected" : "" %>>Image</option>
            </select>

            <label for="postBody">Content</label>
            <textarea id="postBody" name="content" rows="10" placeholder="Write with evidence, context, and clear intent." required><%= submittedContent != null ? submittedContent : "" %></textarea>

            <label for="postImage">Optional image</label>
            <input id="postImage" name="postImage" type="file" accept="image/jpeg,image/png,image/gif,image/webp">
            <p class="form-hint">Upload one JPG, PNG, GIF, or WebP image up to 5 MB. Images still go through moderation with the post.</p>

            <label for="imageAltText">Image description</label>
            <input
                id="imageAltText"
                name="imageAltText"
                type="text"
                placeholder="Describe the image for accessibility"
                value="<%= submittedImageAltText != null ? submittedImageAltText : "" %>"
            >

            <div class="form-actions">
                <button class="button button--ghost" type="submit" name="submissionAction" value="save-draft">Save draft</button>
                <button class="button button--primary" type="submit" name="submissionAction" value="submit-review">Submit for review</button>
            </div>
        </form>
    </section>
</main>
<%@ include file="../common/footer.jsp" %>
