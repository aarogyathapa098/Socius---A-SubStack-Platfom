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
String dashboardPath = "/member/home";
String accountPath = "/member/profile";
String createCommunityPath = "/member/create-community";
if (currentUser != null && "admin".equals(currentUser.getRole())) {
    dashboardPath = "/admin/dashboard";
    accountPath = "/admin/dashboard";
    createCommunityPath = "/admin/manage-communities";
} else if (currentUser != null && "moderator".equals(currentUser.getRole())) {
    dashboardPath = "/moderator/dashboard";
    accountPath = "/moderator/dashboard";
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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
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
            <a class="brand" href="${pageContext.request.contextPath}/discover">Socius</a>
            <nav class="topnav">
                <a href="${pageContext.request.contextPath}/discover">Discover</a>
                <a href="${pageContext.request.contextPath}/community">Communities</a>
                <a href="${pageContext.request.contextPath}/about">Purpose</a>
            </nav>
            <div class="topbar__actions">
                <form class="topbar__search" action="${pageContext.request.contextPath}/search" method="get" autocomplete="off">
                    <span class="material-symbols-outlined">search</span>
                    <input id="globalSearchInput" type="search" name="q" placeholder="Search posts and communities" aria-label="Search posts and communities" aria-expanded="false" aria-controls="globalSearchSuggestions">
                    <div id="globalSearchSuggestions" class="search-suggestions" role="listbox" aria-label="Search suggestions"></div>
                </form>
                <a class="topbar__chip" href="${pageContext.request.contextPath}/contact">
                    <span class="material-symbols-outlined">help</span>
                    <span>Support</span>
                </a>
                <% if (currentUser != null) { %>
                    <a class="topbar__chip topbar__chip--primary" href="${pageContext.request.contextPath}<%= createCommunityPath %>">
                        <span class="material-symbols-outlined">add</span>
                        <span>Add community</span>
                    </a>
                    <a class="topbar__icon-link" href="${pageContext.request.contextPath}/notifications" aria-label="Notifications">
                        <span class="material-symbols-outlined">notifications</span>
                        <span id="notificationCount" class="notification-count" hidden>0</span>
                    </a>
                    <a class="topbar__account" href="${pageContext.request.contextPath}<%= accountPath %>">
                        <span class="material-symbols-outlined">account_circle</span>
                        <span><%= currentUser.getDisplayName() != null ? currentUser.getDisplayName() : currentUser.getUsername() %></span>
                    </a>
                    <a class="topbar__chip topbar__chip--persistent" href="${pageContext.request.contextPath}/logout">Logout</a>
                <% } else { %>
                    <a class="topbar__chip" href="${pageContext.request.contextPath}/register">Join</a>
                    <a class="topbar__account" href="${pageContext.request.contextPath}/login">
                        <span class="material-symbols-outlined">account_circle</span>
                        <span>Sign in</span>
                    </a>
                <% } %>
            </div>
        </div>
    </header>

    <% if (flashSuccess != null) { %>
        <div class="flash flash--success"><%= flashSuccess %></div>
    <% } %>

    <div class="page-frame <%= showSidebar ? "" : "page-frame--full" %>">
