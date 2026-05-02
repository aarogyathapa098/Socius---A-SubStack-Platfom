<%@ page import="java.util.List" %>
<%@ page import="model.Post" %>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>
<%
List<Post> pendingPosts = (List<Post>) request.getAttribute("pendingPosts");
%>
<main class="content-shell">
    <section class="section-header">
        <div>
            <p class="eyebrow">Approval Queue</p>
            <h1>Pending contributions awaiting review.</h1>
        </div>
    </section>

    <section class="section-card">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Title</th>
                    <th>Author</th>
                    <th>Community</th>
                    <th>Submitted</th>
                    <th>Decision</th>
                </tr>
            </thead>
            <tbody>
                <% if (pendingPosts != null && !pendingPosts.isEmpty()) { %>
                    <% for (Post post : pendingPosts) { %>
                        <tr>
                            <td>
                                <strong><%= post.getTitle() %></strong>
                                <% if (post.hasImage()) { %>
                                    <img
                                        class="approval-thumb"
                                        src="${pageContext.request.contextPath}<%= post.getImageUrl() %>"
                                        alt="<%= post.getImageAltText() != null ? post.getImageAltText() : post.getTitle() %>"
                                    >
                                <% } %>
                            </td>
                            <td><%= post.getAuthorUsername() %></td>
                            <td><%= post.getCommunityName() %></td>
                            <td><%= post.getCreatedAt() %></td>
                            <td>
                                <form action="${pageContext.request.contextPath}/moderator/approval-queue" method="post" class="approval-form">
                                    <input type="hidden" name="postId" value="<%= post.getPostId() %>">
                                    <input type="text" name="rejectionReason" placeholder="Reason if rejecting">
                                    <div class="approval-form__actions">
                                        <button class="button button--primary" type="submit" name="decision" value="approve">Approve</button>
                                        <button class="button button--ghost" type="submit" name="decision" value="reject">Reject</button>
                                    </div>
                                </form>
                            </td>
                        </tr>
                    <% } %>
                <% } else { %>
                    <tr>
                        <td colspan="5">No pending posts are waiting for review.</td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </section>
</main>
<%@ include file="../common/footer.jsp" %>
