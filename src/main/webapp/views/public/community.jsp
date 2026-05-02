<%@ page import="java.util.List" %>
<%@ page import="model.Community" %>
<%@ page import="model.Post" %>
<%@ include file="../common/header.jsp" %>
<%
Community selectedCommunity = (Community) request.getAttribute("selectedCommunity");
List<Community> communities = (List<Community>) request.getAttribute("communities");
List<Post> communityPosts = (List<Post>) request.getAttribute("communityPosts");
Integer approvedPostCountAttr = (Integer) request.getAttribute("approvedPostCount");
Boolean communityNotFound = (Boolean) request.getAttribute("communityNotFound");
Boolean communityJoined = (Boolean) request.getAttribute("communityJoined");
String keyword = (String) request.getAttribute("keyword");
int approvedPostCount = approvedPostCountAttr != null ? approvedPostCountAttr.intValue() : 0;
%>
<main class="content-shell content-shell--public">
    <% if (selectedCommunity != null) { %>
        <section class="section-card public-hero">
            <div class="public-hero__copy">
                <p class="eyebrow">Community</p>
                <h1><%= selectedCommunity.getName() %></h1>
                <p class="lead"><%= selectedCommunity.getDescription() %></p>
            </div>
            <div class="public-hero__actions">
                <% if (currentUser != null) { %>
                    <a class="button button--primary" href="${pageContext.request.contextPath}/member/create-post">Create post</a>
                    <% if (communityJoined == null || !communityJoined.booleanValue()) { %>
                        <form action="${pageContext.request.contextPath}/member/join-community" method="post" class="inline-form">
                            <input type="hidden" name="communityId" value="<%= selectedCommunity.getCommunityId() %>">
                            <input type="hidden" name="slug" value="<%= selectedCommunity.getSlug() %>">
                            <button class="button button--ghost" type="submit">Join community</button>
                        </form>
                    <% } else { %>
                        <span class="inline-note">You already joined this community.</span>
                    <% } %>
                <% } else { %>
                    <a class="button button--primary" href="${pageContext.request.contextPath}/login">Sign in to post</a>
                <% } %>
                <a class="button button--ghost" href="${pageContext.request.contextPath}/community">Back to communities</a>
            </div>
        </section>

        <section class="overview-strip">
            <div class="metric-card">
                <strong><%= selectedCommunity.getMemberCount() %></strong>
                <span>members</span>
            </div>
            <div class="metric-card">
                <strong><%= approvedPostCount %></strong>
                <span>approved posts</span>
            </div>
            <div class="metric-card">
                <strong><%= selectedCommunity.isRequiresReview() ? "Yes" : "No" %></strong>
                <span>review required</span>
            </div>
            <div class="metric-card">
                <strong><%= selectedCommunity.isPrivateCommunity() ? "Private" : "Public" %></strong>
                <span>visibility</span>
            </div>
        </section>

        <section class="public-grid">
            <section class="section-card">
                <div class="section-header">
                    <div>
                        <p class="eyebrow">Published discussion</p>
                        <h2>Posts in this community.</h2>
                    </div>
                </div>

                <% if (communityPosts != null && !communityPosts.isEmpty()) { %>
                    <div class="text-card-list">
                        <% for (Post post : communityPosts) { %>
                            <article class="text-card">
                                <div class="text-card__topline">
                                    <span class="badge <%= post.isFeatured() ? "badge--mulberry" : "badge--ink" %>">
                                        <%= post.isFeatured() ? "Featured" : "Approved" %>
                                    </span>
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
                        <p>This community exists, but nothing has been published here yet.</p>
                    </div>
                <% } %>
            </section>

            <aside class="section-card">
                <p class="eyebrow">Community rules</p>
                <h2>Before you contribute</h2>
                <p class="lead-sm">
                    <%= selectedCommunity.getGuidelines() != null && !selectedCommunity.getGuidelines().trim().isEmpty()
                        ? selectedCommunity.getGuidelines()
                        : "This community has not added written guidelines yet." %>
                </p>
            </aside>
        </section>
    <% } else { %>
        <section class="section-card public-hero">
            <div class="public-hero__copy">
                <p class="eyebrow">Communities</p>
                <h1>All communities in Socius.</h1>
                <p class="lead">
                    Choose a community to read its approved posts. If a community has no published posts yet, it will show an honest empty state instead of placeholder content.
                </p>
            </div>
            <div class="public-hero__actions">
                <% if (keyword != null) { %>
                    <span class="inline-note">Results for "<%= keyword %>"</span>
                <% } else { %>
                    <span class="inline-note">Search from the top bar to filter this list.</span>
                <% } %>
                <% if (currentUser != null) { %>
                    <a class="button button--ghost" href="${pageContext.request.contextPath}<%= createCommunityPath %>">Add community</a>
                <% } %>
            </div>
        </section>

        <% if (communityNotFound != null && communityNotFound.booleanValue()) { %>
            <section class="section-card empty-state">
                <h3>That community was not found</h3>
                <p>The requested community slug does not exist, so the full list is shown below instead.</p>
            </section>
        <% } %>

        <section class="section-card">
            <% if (communities != null && !communities.isEmpty()) { %>
                <div class="text-card-list">
                    <% for (Community community : communities) { %>
                        <article class="text-card text-card--community">
                            <div class="text-card__topline">
                                <span class="badge <%= community.isRequiresReview() ? "badge--mulberry" : "badge--sage" %>">
                                    <%= community.isRequiresReview() ? "Reviewed" : "Open" %>
                                </span>
                                <span class="muted"><%= community.getMemberCount() %> members</span>
                            </div>
                            <h3><a href="${pageContext.request.contextPath}/community?slug=<%= community.getSlug() %>"><%= community.getName() %></a></h3>
                            <p><%= community.getDescription() %></p>
                            <div class="entity-meta">
                                <span><%= community.isPrivateCommunity() ? "Private community" : "Public community" %></span>
                                <span><%= community.getSlug() %></span>
                            </div>
                        </article>
                    <% } %>
                </div>
            <% } else { %>
                <div class="empty-state">
                    <h3>No communities matched</h3>
                    <p>Try a different search term or create a new community from the admin area.</p>
                </div>
            <% } %>
        </section>
    <% } %>
</main>
<%@ include file="../common/footer.jsp" %>
