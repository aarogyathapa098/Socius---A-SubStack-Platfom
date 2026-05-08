<%@ page import="java.util.List" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="java.time.ZoneId" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="model.Post" %>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>
<%
List<Post> memberPosts = (List<Post>) request.getAttribute("memberPosts");
ZoneId nepalZone = ZoneId.of("Asia/Kathmandu");
DateTimeFormatter nepalDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
%>
<main class="content-shell">
    <section class="section-header">
        <div>
            <p class="eyebrow">My Posts</p>
            <h1>Drafts, pending items, and published work.</h1>
        </div>
        <a class="button button--primary" href="${pageContext.request.contextPath}/member/create-post">Create post</a>
    </section>

    <section class="section-card">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Title</th>
                    <th>Community</th>
                    <th>Status</th>
                    <th>Updated (Nepal)</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <% if (memberPosts != null && !memberPosts.isEmpty()) { %>
                    <% for (Post post : memberPosts) { %>
                        <tr>
                            <td>
                                <% if ("approved".equals(post.getStatus())) { %>
                                    <a href="${pageContext.request.contextPath}/post?id=<%= post.getPostId() %>"><%= post.getTitle() %></a>
                                <% } else { %>
                                    <%= post.getTitle() %>
                                <% } %>
                                <% if (post.hasImage()) { %>
                                    <span class="badge badge--ink table-badge">Image</span>
                                <% } %>
                            </td>
                            <td><%= post.getCommunityName() %></td>
                            <td>
                                <span class="badge <%= "approved".equals(post.getStatus()) ? "badge--sage" : ("rejected".equals(post.getStatus()) ? "badge--rose" : "badge--amber") %>">
                                    <%= post.getStatus() %>
                                </span>
                            </td>
                            <%
                            Timestamp updatedAt = post.getUpdatedAt() != null ? post.getUpdatedAt() : post.getCreatedAt();
                            String nepalUpdatedAt = updatedAt != null
                                ? updatedAt.toInstant().atZone(nepalZone).format(nepalDateFormatter)
                                : "";
                            %>
                            <td><%= nepalUpdatedAt %></td>
                            <td>
                                <% if ("approved".equals(post.getStatus())) { %>
                                    <a class="text-link" href="${pageContext.request.contextPath}/post?id=<%= post.getPostId() %>">View</a>
                                    <a class="text-link" href="${pageContext.request.contextPath}/member/edit-post?id=<%= post.getPostId() %>">Edit</a>
                                <% } else { %>
                                    <a class="text-link" href="${pageContext.request.contextPath}/member/edit-post?id=<%= post.getPostId() %>">Edit</a>
                                <% } %>
                            </td>
                        </tr>
                    <% } %>
                <% } else { %>
                    <tr>
                        <td colspan="5">No posts found for this member yet.</td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </section>
</main>
<%@ include file="../common/footer.jsp" %>
