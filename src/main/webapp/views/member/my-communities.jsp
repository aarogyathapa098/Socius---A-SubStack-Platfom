<%@ page import="java.util.List" %>
<%@ page import="model.Community" %>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>
<%
List<Community> memberCommunities = (List<Community>) request.getAttribute("memberCommunities");
%>
<main class="content-shell">
    <section class="section-header">
        <div>
            <p class="eyebrow">My Communities</p>
            <h1>Your active circles.</h1>
        </div>
        <a class="button button--primary" href="${pageContext.request.contextPath}/member/create-community">Create community</a>
    </section>

    <section class="section-card">
        <% if (memberCommunities != null && !memberCommunities.isEmpty()) { %>
            <div class="text-card-list">
                <% for (Community community : memberCommunities) { %>
                    <article class="text-card text-card--community">
                        <div class="text-card__topline">
                            <span class="badge <%= community.isRequiresReview() ? "badge--mulberry" : "badge--sage" %>">
                                <%= community.isRequiresReview() ? "Reviewed" : "Open" %>
                            </span>
                            <span class="badge <%= "approved".equals(community.getApprovalStatus()) ? "badge--sage" : ("rejected".equals(community.getApprovalStatus()) ? "badge--rose" : "badge--amber") %>">
                                <%= community.getApprovalStatus() != null ? community.getApprovalStatus() : "approved" %>
                            </span>
                            <span class="muted"><%= community.getMemberCount() %> members</span>
                        </div>
                        <h3>
                            <% if ("approved".equals(community.getApprovalStatus())) { %>
                                <a href="${pageContext.request.contextPath}/community?slug=<%= community.getSlug() %>"><%= community.getName() %></a>
                            <% } else { %>
                                <%= community.getName() %>
                            <% } %>
                        </h3>
                        <p><%= community.getDescription() %></p>
                    </article>
                <% } %>
            </div>
        <% } else { %>
            <div class="empty-state">
                <h3>No joined circles</h3>
                <p>This member has not joined any communities yet.</p>
            </div>
        <% } %>
    </section>
</main>
<%@ include file="../common/footer.jsp" %>
