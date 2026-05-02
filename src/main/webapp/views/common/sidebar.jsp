<%@ page import="model.User" %>
<%
User sidebarUser = null;
if (session != null && session.getAttribute("currentUser") instanceof User) {
    sidebarUser = (User) session.getAttribute("currentUser");
}

String sidebarRole = sidebarUser != null ? sidebarUser.getRole() : "guest";
String sidebarHeading = "Member Hub";
String sidebarMessage = "Move through the public feed, your posts, and your profile dashboard without the clutter.";
String sidebarActionHref = request.getContextPath() + "/member/create-post";
String sidebarActionLabel = "Create post";
String sidebarActionIcon = "edit_square";
String currentPath = request.getRequestURI();

if ("admin".equals(sidebarRole)) {
    sidebarHeading = "Admin Hub";
    sidebarMessage = "Govern users, communities, reports, and moderator assignments from one rail.";
    sidebarActionHref = request.getContextPath() + "/admin/manage-communities";
    sidebarActionLabel = "New community";
    sidebarActionIcon = "add";
} else if ("moderator".equals(sidebarRole)) {
    sidebarHeading = "Moderator Hub";
    sidebarMessage = "Review pending posts, triage reports, and coordinate safe participation.";
    sidebarActionHref = request.getContextPath() + "/moderator/approval-queue";
    sidebarActionLabel = "Review queue";
    sidebarActionIcon = "rule";
}
%>
<aside class="side-rail" aria-label="<%= sidebarHeading %> navigation">
    <div class="side-rail__header">
        <a class="side-rail__mark" href="${pageContext.request.contextPath}/discover">
            <span class="material-symbols-outlined">account_balance</span>
        </a>
        <div>
            <h2><%= sidebarHeading %></h2>
            <p><%= "guest".equals(sidebarRole) ? "Civic community" : sidebarRole + " workspace" %></p>
        </div>
    </div>

    <p class="side-rail__message"><%= sidebarMessage %></p>

    <nav class="side-rail__nav">
        <% if ("admin".equals(sidebarRole)) { %>
            <a class="side-rail__link <%= currentPath.contains("/admin/dashboard") ? "is-active" : "" %>" href="${pageContext.request.contextPath}/admin/dashboard">
                <span class="material-symbols-outlined">dashboard</span>
                <span>Dashboard</span>
            </a>
            <a class="side-rail__link <%= currentPath.contains("/admin/manage-communities") ? "is-active" : "" %>" href="${pageContext.request.contextPath}/admin/manage-communities">
                <span class="material-symbols-outlined">groups</span>
                <span>Communities</span>
            </a>
            <a class="side-rail__link <%= currentPath.contains("/admin/manage-users") ? "is-active" : "" %>" href="${pageContext.request.contextPath}/admin/manage-users">
                <span class="material-symbols-outlined">manage_accounts</span>
                <span>Users</span>
            </a>
            <a class="side-rail__link <%= currentPath.contains("/admin/manage-moderators") ? "is-active" : "" %>" href="${pageContext.request.contextPath}/admin/manage-moderators">
                <span class="material-symbols-outlined">admin_panel_settings</span>
                <span>Moderators</span>
            </a>
            <a class="side-rail__link <%= currentPath.contains("/admin/reports") ? "is-active" : "" %>" href="${pageContext.request.contextPath}/admin/reports">
                <span class="material-symbols-outlined">report</span>
                <span>Reports</span>
            </a>
        <% } else if ("moderator".equals(sidebarRole)) { %>
            <a class="side-rail__link <%= currentPath.contains("/moderator/dashboard") ? "is-active" : "" %>" href="${pageContext.request.contextPath}/moderator/dashboard">
                <span class="material-symbols-outlined">dashboard</span>
                <span>Dashboard</span>
            </a>
            <a class="side-rail__link <%= currentPath.contains("/moderator/approval-queue") ? "is-active" : "" %>" href="${pageContext.request.contextPath}/moderator/approval-queue">
                <span class="material-symbols-outlined">rule</span>
                <span>Approval queue</span>
            </a>
            <a class="side-rail__link <%= currentPath.contains("/moderator/reported-posts") ? "is-active" : "" %>" href="${pageContext.request.contextPath}/moderator/reported-posts">
                <span class="material-symbols-outlined">flag</span>
                <span>Reported posts</span>
            </a>
            <a class="side-rail__link <%= currentPath.contains("/moderator/banned-members") ? "is-active" : "" %>" href="${pageContext.request.contextPath}/moderator/banned-members">
                <span class="material-symbols-outlined">block</span>
                <span>Banned members</span>
            </a>
            <a class="side-rail__link <%= currentPath.contains("/moderator/send-bulletin") ? "is-active" : "" %>" href="${pageContext.request.contextPath}/moderator/send-bulletin">
                <span class="material-symbols-outlined">campaign</span>
                <span>Send bulletin</span>
            </a>
        <% } else { %>
            <a class="side-rail__link <%= currentPath.contains("/member/home") ? "is-active" : "" %>" href="${pageContext.request.contextPath}/member/home">
                <span class="material-symbols-outlined">home</span>
                <span>Home feed</span>
            </a>
            <a class="side-rail__link <%= currentPath.contains("/member/my-posts") ? "is-active" : "" %>" href="${pageContext.request.contextPath}/member/my-posts">
                <span class="material-symbols-outlined">article</span>
                <span>My posts</span>
            </a>
            <a class="side-rail__link <%= currentPath.contains("/member/my-communities") ? "is-active" : "" %>" href="${pageContext.request.contextPath}/member/my-communities">
                <span class="material-symbols-outlined">diversity_3</span>
                <span>My communities</span>
            </a>
            <a class="side-rail__link <%= currentPath.contains("/member/create-post") ? "is-active" : "" %>" href="${pageContext.request.contextPath}/member/create-post">
                <span class="material-symbols-outlined">edit_square</span>
                <span>Create post</span>
            </a>
            <a class="side-rail__link <%= currentPath.contains("/member/create-community") ? "is-active" : "" %>" href="${pageContext.request.contextPath}/member/create-community">
                <span class="material-symbols-outlined">add_circle</span>
                <span>Create community</span>
            </a>
            <a class="side-rail__link <%= currentPath.contains("/member/profile") || currentPath.contains("/member/dashboard") ? "is-active" : "" %>" href="${pageContext.request.contextPath}/member/profile">
                <span class="material-symbols-outlined">person</span>
                <span>Profile dashboard</span>
            </a>
            <a class="side-rail__link <%= currentPath.contains("/notifications") ? "is-active" : "" %>" href="${pageContext.request.contextPath}/notifications">
                <span class="material-symbols-outlined">notifications</span>
                <span>Notifications</span>
            </a>
        <% } %>
    </nav>

    <div class="side-rail__card">
        <p>Access model</p>
        <strong><%= "admin".equals(sidebarRole) ? "Governance" : ("moderator".equals(sidebarRole) ? "Moderation" : "Participation") %></strong>
        <span>
            <%= "admin".equals(sidebarRole)
                ? "System-wide controls and oversight."
                : ("moderator".equals(sidebarRole)
                    ? "Review, reports, bans, and bulletins."
                    : "Writing, joining, and community building.") %>
        </span>
    </div>

    <div class="side-rail__footer">
        <a class="button button--primary" href="<%= sidebarActionHref %>">
            <span class="material-symbols-outlined"><%= sidebarActionIcon %></span>
            <span><%= sidebarActionLabel %></span>
        </a>
        <a class="side-rail__utility" href="${pageContext.request.contextPath}/contact">
            <span class="material-symbols-outlined">help</span>
            <span>Help center</span>
        </a>
        <a class="side-rail__utility" href="${pageContext.request.contextPath}/logout">
            <span class="material-symbols-outlined">logout</span>
            <span>Logout</span>
        </a>
    </div>
</aside>
