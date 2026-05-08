<%@ page import="model.User" %>
<%
String pageTitle = (String) request.getAttribute("pageTitle");
if (pageTitle == null || pageTitle.trim().isEmpty()) {
    pageTitle = "Socius";
}
User currentUser = null;
if (session != null && session.getAttribute("currentUser") instanceof User) {
    currentUser = (User) session.getAttribute("currentUser");
}
Boolean showSidebarAttr = (Boolean) request.getAttribute("showSidebar");
boolean showSidebar = showSidebarAttr == null ? true : showSidebarAttr.booleanValue();
String currentHeaderPath = request.getRequestURI();
String headerServletPath = request.getServletPath();
Object forwardedHeaderUriAttr = request.getAttribute("jakarta.servlet.forward.request_uri");
Object forwardedHeaderServletPathAttr = request.getAttribute("jakarta.servlet.forward.servlet_path");
String forwardedHeaderUri = forwardedHeaderUriAttr != null ? String.valueOf(forwardedHeaderUriAttr) : "";
String forwardedHeaderServletPath = forwardedHeaderServletPathAttr != null ? String.valueOf(forwardedHeaderServletPathAttr) : "";
String headerRoutePath = currentHeaderPath + " " + headerServletPath + " " + forwardedHeaderUri + " " + forwardedHeaderServletPath;
boolean adminPortalHeader = headerRoutePath.contains("/admin/") || headerRoutePath.contains("/views/admin/");
boolean moderatorPortalHeader = headerRoutePath.contains("/moderator/") || headerRoutePath.contains("/views/mod/");
String currentUserRole = currentUser != null ? currentUser.getRole() : "";
boolean adminUserHeader = "admin".equals(currentUserRole);
boolean moderatorUserHeader = "moderator".equals(currentUserRole) || adminUserHeader;
boolean authPageHeader = currentHeaderPath.contains("/login")
    || currentHeaderPath.contains("/register")
    || currentHeaderPath.contains("/forgot-password")
    || currentHeaderPath.contains("/change-password");
String brandPath = currentUser != null ? "/member/home" : (authPageHeader ? "/login" : "/discover");
String accountPath = "/member/profile";
String createCommunityPath = "/member/create-community";
String profileMenuLabel = "Profile";
if (adminPortalHeader) {
    accountPath = "/admin/dashboard";
    createCommunityPath = "/admin/manage-communities";
    profileMenuLabel = "Admin";
} else if (moderatorPortalHeader) {
    accountPath = "/moderator/dashboard";
    createCommunityPath = "/moderator/approval-queue";
    profileMenuLabel = "Moderator";
} else if (adminUserHeader) {
    profileMenuLabel = "Admin";
} else if (moderatorUserHeader) {
    profileMenuLabel = "Moderator";
}
String flashSuccess = null;
if (session != null && session.getAttribute("flashSuccess") != null) {
    flashSuccess = String.valueOf(session.getAttribute("flashSuccess"));
    session.removeAttribute("flashSuccess");
}
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= pageTitle %> | Socius</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Newsreader:ital,wght@0,400;0,500;0,600;1,400;1,500&family=Inter:wght@400;500;600;700;800&family=Material+Symbols+Outlined:wght@300;400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=home-width-1">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/discover.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/community.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/post.css">
    <script>
        const CTX = '${pageContext.request.contextPath}';
    </script>
</head>
<body class="socius-body <%= showSidebar ? "socius-body--with-sidebar" : "socius-body--public" %>">
    <header class="topbar glass-surface">
        <div class="topbar__inner">
            <div class="topbar__left">
                <a class="brand" href="${pageContext.request.contextPath}<%= brandPath %>">Socius</a>
                <% if (!authPageHeader) { %>
                    <nav class="topnav" aria-label="Primary navigation">
                        <% if (currentUser != null) { %>
                            <a href="${pageContext.request.contextPath}/member/home">Home</a>
                        <% } %>
                        <a href="${pageContext.request.contextPath}/discover">Discover</a>
                        <a href="${pageContext.request.contextPath}/community">Communities</a>
                        <a href="${pageContext.request.contextPath}/about">Purpose</a>
                    </nav>
                <% } %>
            </div>
            <% if (!authPageHeader) { %>
                <form class="topbar__search" action="${pageContext.request.contextPath}/search" method="get" autocomplete="off">
                    <span class="material-symbols-outlined">search</span>
                    <input id="globalSearchInput" type="search" name="q" placeholder="Search posts and communities" aria-label="Search posts and communities" aria-expanded="false" aria-controls="globalSearchSuggestions">
                    <div id="globalSearchSuggestions" class="search-suggestions" role="listbox" aria-label="Search suggestions"></div>
                </form>
            <% } %>
            <div class="topbar__actions">
                <% if (currentUser != null) { %>
                    <a class="topbar__icon-link" href="${pageContext.request.contextPath}/notifications" aria-label="Notifications">
                        <span class="material-symbols-outlined">notifications</span>
                        <span id="notificationCount" class="notification-count" hidden>0</span>
                    </a>
                    <details class="profile-menu">
                        <summary class="topbar__account">
                            <span class="material-symbols-outlined">account_circle</span>
                            <span><%= profileMenuLabel %></span>
                            <span class="material-symbols-outlined">expand_more</span>
                        </summary>
                        <div class="profile-menu__panel">
                            <% if (adminUserHeader) { %>
                                <a href="${pageContext.request.contextPath}/admin/dashboard">Admin Dashboard</a>
                            <% } %>
                            <% if (moderatorUserHeader) { %>
                                <a href="${pageContext.request.contextPath}/moderator/dashboard">Moderator Dashboard</a>
                            <% } %>
                            <a href="${pageContext.request.contextPath}/member/profile">Profile</a>
                            <a href="${pageContext.request.contextPath}/contact">Support</a>
                            <a href="${pageContext.request.contextPath}/logout">Logout</a>
                        </div>
                    </details>
                <% } %>
                <% if (!authPageHeader) { %>
                    <details class="topbar__mobile-menu">
                        <summary aria-label="Open navigation menu">
                            <span class="material-symbols-outlined">menu</span>
                        </summary>
                        <div class="topbar__mobile-panel">
                            <% if (currentUser != null) { %>
                                <a href="${pageContext.request.contextPath}/member/home">Home</a>
                            <% } %>
                            <a href="${pageContext.request.contextPath}/discover">Discover</a>
                            <a href="${pageContext.request.contextPath}/community">Communities</a>
                            <a href="${pageContext.request.contextPath}/about">Purpose</a>
                            <a href="${pageContext.request.contextPath}/contact">Support</a>
                        </div>
                    </details>
                <% } %>
            </div>
        </div>
    </header>

    <% if (flashSuccess != null) { %>
        <div class="flash flash--success"><%= flashSuccess %></div>
    <% } %>

    <div class="page-frame <%= showSidebar ? "" : "page-frame--full" %>">
