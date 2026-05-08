<%@ page import="java.util.List" %>
<%@ page import="model.Post" %>
<%@ page import="model.User" %>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>
<%
Integer adminUserCount = (Integer) request.getAttribute("adminUserCount");
Integer adminCommunityCount = (Integer) request.getAttribute("adminCommunityCount");
Integer adminPendingCommunityCount = (Integer) request.getAttribute("adminPendingCommunityCount");
Integer adminPendingCount = (Integer) request.getAttribute("adminPendingCount");
Integer adminReportCount = (Integer) request.getAttribute("adminReportCount");
List<User> adminRecentUsers = (List<User>) request.getAttribute("adminRecentUsers");
List<Post> adminRecentPosts = (List<Post>) request.getAttribute("adminRecentPosts");
%>
<main class="content-shell">
    <section class="section-card section-card--hero-copy">
        <p class="eyebrow">Admin Dashboard</p>
        <h1>Govern the full Socius system.</h1>
        <p class="lead-sm">This area is designed for CRUD, role management, moderation oversight, and recent platform activity.</p>
    </section>

    <section class="metric-grid">
        <div class="metric-card"><strong><%= adminUserCount != null ? adminUserCount.intValue() : 0 %></strong><span>Total users</span></div>
        <div class="metric-card"><strong><%= adminCommunityCount != null ? adminCommunityCount.intValue() : 0 %></strong><span>Communities</span></div>
        <div class="metric-card"><strong><%= adminPendingCommunityCount != null ? adminPendingCommunityCount.intValue() : 0 %></strong><span>Pending communities</span></div>
        <div class="metric-card"><strong><%= adminPendingCount != null ? adminPendingCount.intValue() : 0 %></strong><span>Pending posts</span></div>
        <div class="metric-card"><strong><%= adminReportCount != null ? adminReportCount.intValue() : 0 %></strong><span>Open reports</span></div>
    </section>

    <section class="split-section">
        <div class="section-card">
            <p class="eyebrow">Quick Links</p>
            <div class="stack-list">
                <a class="list-row" href="${pageContext.request.contextPath}/admin/manage-communities"><strong>Manage communities</strong><span><%= adminPendingCommunityCount != null ? adminPendingCommunityCount.intValue() : 0 %> pending</span></a>
                <a class="list-row" href="${pageContext.request.contextPath}/admin/manage-users"><strong>Manage users</strong><span>roles and penalties</span></a>
                <a class="list-row" href="${pageContext.request.contextPath}/admin/manage-moderators"><strong>Manage moderators</strong><span>assign and remove</span></a>
                <a class="list-row" href="${pageContext.request.contextPath}/admin/reports"><strong>Open reports</strong><span><%= adminReportCount != null ? adminReportCount.intValue() : 0 %> items</span></a>
            </div>
        </div>
        <div class="section-card">
            <p class="eyebrow">Recent Records</p>
            <ul class="bullet-list">
                <% if (adminRecentUsers != null) { %>
                    <% for (User user : adminRecentUsers) { %>
                        <li>User <strong><%= user.getUsername() %></strong> joined with role <strong><%= user.getRole() %></strong>.</li>
                    <% } %>
                <% } %>
                <% if (adminRecentPosts != null) { %>
                    <% for (Post post : adminRecentPosts) { %>
                        <li>
                            Post
                            <strong>
                                <% if ("approved".equals(post.getStatus())) { %>
                                    <a href="${pageContext.request.contextPath}/post?id=<%= post.getPostId() %>"><%= post.getTitle() %></a>
                                <% } else { %>
                                    <%= post.getTitle() %>
                                <% } %>
                            </strong>
                            was created in <strong><%= post.getCommunityName() %></strong>.
                        </li>
                    <% } %>
                <% } %>
            </ul>
        </div>
    </section>
</main>
<%@ include file="../common/footer.jsp" %>
