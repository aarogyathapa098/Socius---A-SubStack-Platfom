<%@ page import="java.util.List" %>
<%@ page import="model.Community" %>
<%@ page import="model.Post" %>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>
<%
List<Post> memberPosts = (List<Post>) request.getAttribute("memberPosts");
List<Community> memberCommunities = (List<Community>) request.getAttribute("memberCommunities");
Integer memberPostCount = (Integer) request.getAttribute("memberPostCount");
Integer memberPendingCount = (Integer) request.getAttribute("memberPendingCount");
Integer memberCommunityCount = (Integer) request.getAttribute("memberCommunityCount");
%>
<main class="content-shell">
    <section class="section-card section-card--hero-copy">
        <p class="eyebrow">Member Dashboard</p>
        <h1>Welcome back to your Socius workspace.</h1>
        <p class="lead-sm">Track pending posts, active communities, and recent account activity from one place.</p>
    </section>

    <section class="metric-grid">
        <div class="metric-card"><strong><%= memberPostCount != null ? memberPostCount.intValue() : 0 %></strong><span>Your posts</span></div>
        <div class="metric-card"><strong><%= memberPendingCount != null ? memberPendingCount.intValue() : 0 %></strong><span>Pending review</span></div>
        <div class="metric-card"><strong><%= memberCommunityCount != null ? memberCommunityCount.intValue() : 0 %></strong><span>Joined circles</span></div>
        <div class="metric-card"><strong><%= currentUser != null ? currentUser.getPenaltyPoints() : 0 %></strong><span>Penalty points</span></div>
    </section>

    <section class="split-section">
        <div class="section-card">
            <div class="section-header">
                <div>
                    <p class="eyebrow">Recent Posts</p>
                    <h2>Your publishing queue.</h2>
                </div>
                <a class="text-link" href="${pageContext.request.contextPath}/member/my-posts">See all</a>
            </div>
            <div class="stack-list">
                <% if (memberPosts != null && !memberPosts.isEmpty()) { %>
                    <% for (Post post : memberPosts) { %>
                        <div class="list-row">
                            <div>
                                <strong>
                                    <% if ("approved".equals(post.getStatus())) { %>
                                        <a href="${pageContext.request.contextPath}/post?id=<%= post.getPostId() %>"><%= post.getTitle() %></a>
                                    <% } else { %>
                                        <%= post.getTitle() %>
                                    <% } %>
                                </strong>
                                <p class="muted"><%= post.getCommunityName() %></p>
                                <% if (post.hasImage()) { %>
                                    <span class="badge badge--ink">Image attached</span>
                                <% } %>
                            </div>
                            <span class="badge <%= "approved".equals(post.getStatus()) ? "badge--sage" : "badge--amber" %>">
                                <%= post.getStatus() %>
                            </span>
                        </div>
                    <% } %>
                <% } else { %>
                    <div class="list-row">
                        <div>
                            <strong>No posts yet</strong>
                            <p class="muted">Create your first post to start building activity.</p>
                        </div>
                        <span class="badge badge--ink">Empty</span>
                    </div>
                <% } %>
            </div>
        </div>

        <div class="section-card">
            <div class="section-header">
                <div>
                    <p class="eyebrow">Joined Circles</p>
                    <h2>Your communities.</h2>
                </div>
                <a class="text-link" href="${pageContext.request.contextPath}/member/create-community">Create one</a>
            </div>
            <div class="text-card-list">
                <% if (memberCommunities != null && !memberCommunities.isEmpty()) { %>
                    <% for (Community community : memberCommunities) { %>
                        <article class="text-card text-card--compact">
                            <div class="text-card__topline">
                                <span class="badge <%= community.isRequiresReview() ? "badge--mulberry" : "badge--sage" %>">
                                    <%= community.isRequiresReview() ? "Reviewed" : "Open" %>
                                </span>
                                <span class="muted"><%= community.getMemberCount() %> members</span>
                            </div>
                            <h3><a href="${pageContext.request.contextPath}/community?slug=<%= community.getSlug() %>"><%= community.getName() %></a></h3>
                            <p><%= community.getDescription() %></p>
                        </article>
                    <% } %>
                <% } else { %>
                    <div class="empty-state empty-state--soft">
                        <h3>No joined circles</h3>
                        <p>Join a community to see it listed here.</p>
                    </div>
                <% } %>
            </div>
        </div>
    </section>
</main>
<%@ include file="../common/footer.jsp" %>
