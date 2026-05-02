<%@ page import="java.util.List" %>
<%@ page import="model.Notification" %>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>
<%
List<Notification> notifications = (List<Notification>) request.getAttribute("notifications");
%>
<main class="content-shell">
    <section class="section-card section-card--hero-copy">
        <p class="eyebrow">Notifications</p>
        <h1>Your latest Socius updates.</h1>
        <p class="lead-sm">Post decisions, reports, comments, and community approvals appear here.</p>
    </section>

    <section class="section-card">
        <div class="stack-list">
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
            <% } else { %>
                <div class="empty-state empty-state--soft">
                    <h3>No notifications yet</h3>
                    <p>When someone comments, reviews your post, or approves your community, the update will appear here.</p>
                </div>
            <% } %>
        </div>
    </section>
</main>
<%@ include file="../common/footer.jsp" %>
