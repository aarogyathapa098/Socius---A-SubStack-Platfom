<%@ page import="java.util.List" %>
<%@ page import="model.Notification" %>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>
<%
List<Notification> notifications = (List<Notification>) request.getAttribute("notifications");
Integer pendingPostWorkCount = (Integer) request.getAttribute("pendingPostWorkCount");
Integer openReportWorkCount = (Integer) request.getAttribute("openReportWorkCount");
Integer pendingCommunityWorkCount = (Integer) request.getAttribute("pendingCommunityWorkCount");
int pendingPostWork = pendingPostWorkCount != null ? pendingPostWorkCount.intValue() : 0;
int openReportWork = openReportWorkCount != null ? openReportWorkCount.intValue() : 0;
int pendingCommunityWork = pendingCommunityWorkCount != null ? pendingCommunityWorkCount.intValue() : 0;
boolean hasWorkItems = pendingPostWork > 0 || openReportWork > 0 || pendingCommunityWork > 0;
boolean notificationAdminView = currentUser != null && "admin".equals(currentUser.getRole());
%>
<main class="content-shell">
    <section class="section-card section-card--hero-copy">
        <p class="eyebrow">Notifications</p>
        <h1>Your latest Socius updates.</h1>
        <p class="lead-sm">Post decisions, reports, comments, and community approvals appear here.</p>
    </section>

    <section class="section-card">
        <div class="stack-list">
            <% if (pendingCommunityWork > 0) { %>
                <a class="list-row notification-row notification-row--unread" href="${pageContext.request.contextPath}/admin/manage-communities">
                    <div>
                        <strong><%= pendingCommunityWork %> communit<%= pendingCommunityWork == 1 ? "y" : "ies" %> waiting for approval</strong>
                        <p class="muted">Review pending community requests from Admin.</p>
                    </div>
                    <span class="material-symbols-outlined">arrow_forward</span>
                </a>
            <% } %>
            <% if (pendingPostWork > 0) { %>
                <a class="list-row notification-row notification-row--unread" href="${pageContext.request.contextPath}/moderator/approval-queue">
                    <div>
                        <strong><%= pendingPostWork %> post<%= pendingPostWork == 1 ? "" : "s" %> waiting for review</strong>
                        <p class="muted">Open the moderator approval queue.</p>
                    </div>
                    <span class="material-symbols-outlined">arrow_forward</span>
                </a>
            <% } %>
            <% if (openReportWork > 0) { %>
                <a class="list-row notification-row notification-row--unread" href="${pageContext.request.contextPath}<%= notificationAdminView ? "/admin/reports" : "/moderator/reported-posts" %>">
                    <div>
                        <strong><%= openReportWork %> open report<%= openReportWork == 1 ? "" : "s" %> need review</strong>
                        <p class="muted">Review reported posts from <%= notificationAdminView ? "Admin" : "Moderator" %>.</p>
                    </div>
                    <span class="material-symbols-outlined">arrow_forward</span>
                </a>
            <% } %>
            <% if (notifications != null && !notifications.isEmpty()) { %>
                <% for (Notification notification : notifications) { %>
                    <a class="list-row notification-row <%= notification.isRead() ? "" : "notification-row--unread" %>"
                       href="${pageContext.request.contextPath}<%= notification.getTargetUrl() != null ? notification.getTargetUrl() : "/notifications" %>">
                        <div>
                            <strong><%= notification.getMessage() %></strong>
                            <p class="muted"><%= notification.getCreatedAt() %></p>
                        </div>
                        <span class="material-symbols-outlined">arrow_forward</span>
                    </a>
                <% } %>
            <% } else if (!hasWorkItems) { %>
                <div class="empty-state empty-state--soft">
                    <h3>No notifications yet</h3>
                    <p>When someone comments, reviews your post, or approves your community, the update will appear here.</p>
                </div>
            <% } %>
        </div>
    </section>
</main>
<%@ include file="../common/footer.jsp" %>
