<%@ page import="java.util.List" %>
<%@ page import="model.Community" %>
<%@ page import="model.Post" %>
<%@ include file="../common/header.jsp" %>
<%
List<Community> featuredCommunities = (List<Community>) request.getAttribute("featuredCommunities");
List<Post> recentPosts = (List<Post>) request.getAttribute("recentPosts");
Post featurePost = (Post) request.getAttribute("featurePost");
Integer communityCountAttr = (Integer) request.getAttribute("communityCount");
Integer approvedPostCountAttr = (Integer) request.getAttribute("approvedPostCount");
Integer pendingPostCountAttr = (Integer) request.getAttribute("pendingPostCount");
Integer openReportCountAttr = (Integer) request.getAttribute("openReportCount");

int communityCount = communityCountAttr != null ? communityCountAttr.intValue() : 0;
int approvedPostCount = approvedPostCountAttr != null ? approvedPostCountAttr.intValue() : 0;
int pendingPostCount = pendingPostCountAttr != null ? pendingPostCountAttr.intValue() : 0;
int openReportCount = openReportCountAttr != null ? openReportCountAttr.intValue() : 0;
%>
<main class="content-shell content-shell--public">
    <section class="section-card public-hero">
        <div class="public-hero__copy">
            <p class="eyebrow">Discover</p>
            <h1>Cleaner discussion starts with real communities and real moderation.</h1>
            <p class="lead">
                Socius now reads directly from the database, so the homepage only shows communities and approved posts that actually exist.
            </p>
        </div>
        <div class="public-hero__actions">
            <a class="button button--primary" href="${pageContext.request.contextPath}/community">Browse communities</a>
            <% if (currentUser != null) { %>
                <a class="button button--ghost" href="${pageContext.request.contextPath}/member/home">Open workspace</a>
                <a class="button button--ghost" href="${pageContext.request.contextPath}<%= createCommunityPath %>">Add community</a>
            <% } else { %>
                <a class="button button--ghost" href="${pageContext.request.contextPath}/login">Sign in</a>
            <% } %>
        </div>
    </section>

    <section class="overview-strip">
        <div class="metric-card">
            <strong><%= communityCount %></strong>
            <span>communities</span>
        </div>
        <div class="metric-card">
            <strong><%= approvedPostCount %></strong>
            <span>approved posts</span>
        </div>
        <div class="metric-card">
            <strong><%= pendingPostCount %></strong>
            <span>pending review</span>
        </div>
        <div class="metric-card">
            <strong><%= openReportCount %></strong>
            <span>open reports</span>
        </div>
    </section>

    <% if (featurePost != null) { %>
        <section class="section-card spotlight-card">
            <div class="section-header">
                <div>
                    <p class="eyebrow">Featured post</p>
                    <h2><a href="${pageContext.request.contextPath}/post?id=<%= featurePost.getPostId() %>"><%= featurePost.getTitle() %></a></h2>
                </div>
                <a class="text-link" href="${pageContext.request.contextPath}/community?slug=<%= featurePost.getCommunitySlug() %>"><%= featurePost.getCommunityName() %></a>
            </div>
            <% if (featurePost.hasImage()) { %>
                <a class="spotlight-card__image" href="${pageContext.request.contextPath}/post?id=<%= featurePost.getPostId() %>">
                    <img
                        src="${pageContext.request.contextPath}<%= featurePost.getImageUrl() %>"
                        alt="<%= featurePost.getImageAltText() != null ? featurePost.getImageAltText() : featurePost.getTitle() %>"
                    >
                </a>
            <% } %>
            <p class="lead-sm"><%= featurePost.getContent() %></p>
            <div class="entity-meta">
                <span><%= featurePost.getAuthorUsername() %></span>
                <span><%= featurePost.getCommentCount() %> comments</span>
                <span><%= featurePost.getUpvotes() %> upvotes</span>
            </div>
        </section>
    <% } %>

    <section class="public-grid">
        <section class="section-card">
            <div class="section-header">
                <div>
                    <p class="eyebrow">Communities</p>
                    <h2>Browse active spaces.</h2>
                </div>
                <a class="text-link" href="${pageContext.request.contextPath}/community">View all</a>
            </div>

            <% if (featuredCommunities != null && !featuredCommunities.isEmpty()) { %>
                <div class="text-card-list">
                    <% for (Community community : featuredCommunities) { %>
                        <article class="text-card">
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
                </div>
            <% } else { %>
                <div class="empty-state">
                    <h3>No communities yet</h3>
                    <p>Create a community from your workspace to make it appear here after approval.</p>
                </div>
            <% } %>
        </section>

        <section class="section-card">
            <div class="section-header">
                <div>
                    <p class="eyebrow">Approved posts</p>
                    <h2>Latest published discussions.</h2>
                </div>
            </div>

            <% if (recentPosts != null && !recentPosts.isEmpty()) { %>
                <div class="text-card-list">
                    <% for (Post post : recentPosts) { %>
                        <article class="text-card">
                            <div class="text-card__topline">
                                <span class="badge badge--ink"><%= post.getCommunityName() %></span>
                                <span class="muted"><%= post.getAuthorUsername() %></span>
                            </div>
                            <h3><a href="${pageContext.request.contextPath}/post?id=<%= post.getPostId() %>"><%= post.getTitle() %></a></h3>
                            <% if (post.hasImage()) { %>
                                <a class="text-card__image" href="${pageContext.request.contextPath}/post?id=<%= post.getPostId() %>">
                                    <img
                                        src="${pageContext.request.contextPath}<%= post.getImageUrl() %>"
                                        alt="<%= post.getImageAltText() != null ? post.getImageAltText() : post.getTitle() %>"
                                    >
                                </a>
                            <% } %>
                            <p><%= post.getContent() %></p>
                            <div class="entity-meta">
                                <span><%= post.getCommentCount() %> comments</span>
                                <span><%= post.getUpvotes() %> upvotes</span>
                            </div>
                        </article>
                    <% } %>
                </div>
            <% } else { %>
                <div class="empty-state">
                    <h3>No approved posts yet</h3>
                    <p>Posts will show here after a member submits one and it is approved.</p>
                </div>
            <% } %>
        </section>
    </section>
</main>
<%@ include file="../common/footer.jsp" %>
