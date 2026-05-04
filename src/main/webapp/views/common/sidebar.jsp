<%@ page import="model.User" %>
<%
User sidebarUser = null;
if (session != null && session.getAttribute("currentUser") instanceof User) {
    sidebarUser = (User) session.getAttribute("currentUser");
}

String sidebarRole = sidebarUser != null ? sidebarUser.getRole() : "guest";
String currentPath = request.getRequestURI();
String servletPath = request.getServletPath();
Object forwardedUriAttr = request.getAttribute("jakarta.servlet.forward.request_uri");
Object forwardedServletPathAttr = request.getAttribute("jakarta.servlet.forward.servlet_path");
String forwardedUri = forwardedUriAttr != null ? String.valueOf(forwardedUriAttr) : "";
String forwardedServletPath = forwardedServletPathAttr != null ? String.valueOf(forwardedServletPathAttr) : "";
String routePath = currentPath + " " + servletPath + " " + forwardedUri + " " + forwardedServletPath;
boolean adminPortal = routePath.contains("/admin/") || routePath.contains("/views/admin/");
boolean moderatorPortal = routePath.contains("/moderator/") || routePath.contains("/views/mod/");
boolean canModerate = "moderator".equals(sidebarRole) || "admin".equals(sidebarRole);
boolean canAdmin = "admin".equals(sidebarRole);

String sidebarHeading = adminPortal ? "Admin Hub" : (moderatorPortal ? "Moderator Hub" : "Member Hub");
String sidebarMessage = adminPortal
    ? "System controls for communities, users, moderators, and reports."
    : (moderatorPortal
        ? "Moderation controls for reviews, reports, bans, and bulletins."
        : "Move through the public feed, your posts, communities, and profile with the same workspace across every role.");
%>
<aside class="side-rail" aria-label="<%= sidebarHeading %> navigation">
    <div class="side-rail__header">
        <a class="side-rail__mark" href="${pageContext.request.contextPath}<%= adminPortal ? "/admin/dashboard" : (moderatorPortal ? "/moderator/dashboard" : "/member/home") %>">
            <span class="material-symbols-outlined"><%= adminPortal ? "admin_panel_settings" : (moderatorPortal ? "shield_person" : "account_balance") %></span>
        </a>
        <div>
            <h2><%= sidebarHeading %></h2>
            <p><%= "guest".equals(sidebarRole) ? "Civic community" : sidebarRole + " workspace" %></p>
        </div>
    </div>

    <p class="side-rail__message"><%= sidebarMessage %></p>

    <nav class="side-rail__nav">
        <% if (adminPortal) { %>
            <a class="side-rail__link <%= currentPath.contains("/admin/dashboard") ? "is-active" : "" %>" href="${pageContext.request.contextPath}/admin/dashboard">
                <span class="material-symbols-outlined">dashboard</span>
                <span>Admin Dashboard</span>
            </a>
            <a class="side-rail__link side-rail__link--role" href="${pageContext.request.contextPath}/moderator/dashboard" target="_blank" rel="noopener">
                <span class="material-symbols-outlined">shield_person</span>
                <span>Moderator Portal</span>
            </a>
            <a class="side-rail__link <%= currentPath.contains("/admin/manage-communities") ? "is-active" : "" %>" href="${pageContext.request.contextPath}/admin/manage-communities">
                <span class="material-symbols-outlined">groups</span>
                <span>Manage communities</span>
            </a>
            <a class="side-rail__link <%= currentPath.contains("/admin/manage-users") ? "is-active" : "" %>" href="${pageContext.request.contextPath}/admin/manage-users">
                <span class="material-symbols-outlined">manage_accounts</span>
                <span>Manage users</span>
            </a>
            <a class="side-rail__link <%= currentPath.contains("/admin/manage-moderators") ? "is-active" : "" %>" href="${pageContext.request.contextPath}/admin/manage-moderators">
                <span class="material-symbols-outlined">admin_panel_settings</span>
                <span>Manage moderators</span>
            </a>
            <a class="side-rail__link <%= currentPath.contains("/admin/reports") ? "is-active" : "" %>" href="${pageContext.request.contextPath}/admin/reports">
                <span class="material-symbols-outlined">report</span>
                <span>Reports</span>
            </a>
        <% } else if (moderatorPortal) { %>
            <a class="side-rail__link <%= currentPath.contains("/moderator/dashboard") ? "is-active" : "" %>" href="${pageContext.request.contextPath}/moderator/dashboard">
                <span class="material-symbols-outlined">dashboard</span>
                <span>Moderator Dashboard</span>
            </a>
            <% if (canAdmin) { %>
                <a class="side-rail__link side-rail__link--role" href="${pageContext.request.contextPath}/admin/dashboard" target="_blank" rel="noopener">
                    <span class="material-symbols-outlined">admin_panel_settings</span>
                    <span>Admin Portal</span>
                </a>
            <% } %>
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

            <% if (canModerate) { %>
                <a class="side-rail__link side-rail__link--role" href="${pageContext.request.contextPath}/moderator/dashboard" target="_blank" rel="noopener">
                    <span class="material-symbols-outlined">shield_person</span>
                    <span>Moderator Dashboard</span>
                </a>
            <% } %>

            <% if (canAdmin) { %>
                <a class="side-rail__link side-rail__link--role" href="${pageContext.request.contextPath}/admin/dashboard" target="_blank" rel="noopener">
                    <span class="material-symbols-outlined">admin_panel_settings</span>
                    <span>Admin Dashboard</span>
                </a>
            <% } %>
        <% } %>
    </nav>

    <div class="side-rail__card">
        <p>Access model</p>
        <strong><%= adminPortal ? "Administration" : (moderatorPortal ? "Moderation" : "Participation") %></strong>
        <span>
            <%= adminPortal
                ? "Only admin controls are shown in this portal."
                : (moderatorPortal
                    ? "Only moderator controls are shown in this portal."
                    : "Writing, joining, profile management, and notifications stay consistent for every signed-in user.") %>
        </span>
    </div>

    <div class="side-rail__footer">
        <% if (adminPortal) { %>
            <a class="button button--primary" href="${pageContext.request.contextPath}/admin/manage-communities">
                <span class="material-symbols-outlined">add</span>
                <span>New community</span>
            </a>
        <% } else if (moderatorPortal) { %>
            <a class="button button--primary" href="${pageContext.request.contextPath}/moderator/approval-queue">
                <span class="material-symbols-outlined">rule</span>
                <span>Review queue</span>
            </a>
        <% } else { %>
            <a class="button button--primary" href="${pageContext.request.contextPath}/member/create-post">
                <span class="material-symbols-outlined">edit_square</span>
                <span>Create post</span>
            </a>
            <a class="side-rail__utility" href="${pageContext.request.contextPath}/contact">
                <span class="material-symbols-outlined">help</span>
                <span>Help center</span>
            </a>
        <% } %>
        <a class="side-rail__utility" href="${pageContext.request.contextPath}/logout">
            <span class="material-symbols-outlined">logout</span>
            <span>Logout</span>
        </a>
    </div>
</aside>
