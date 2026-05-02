<%@ page import="java.util.List" %>
<%@ page import="model.Community" %>
<%@ page import="model.Post" %>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>
<%
List<Post> feedPosts = (List<Post>) request.getAttribute("feedPosts");
List<Community> suggestedCommunities = (List<Community>) request.getAttribute("suggestedCommunities");
String feedMode = (String) request.getAttribute("feedMode");
if (feedMode == null) {
    feedMode = "personalized";
}
%>
<main class="content-shell">
    <section class="feed-shell">
        <div class="feed-stream">
            <div class="feed-heading">
                <div>
                    <p class="eyebrow">Home</p>
                    <h1><%= "explore".equals(feedMode) ? "Explore every community." : "Your ranked home feed." %></h1>
                    <p class="lead-sm">Scroll through approved posts ranked by votes, comments, recency, and the communities you joined.</p>
                </div>
                <a class="button button--primary" href="${pageContext.request.contextPath}/member/create-post">
                    <span class="material-symbols-outlined">edit_square</span>
                    <span>Create post</span>
                </a>
            </div>

            <% if (feedPosts != null && !feedPosts.isEmpty()) { %>
                <div class="feed-tabs" aria-label="Feed filters">
                    <a class="feed-tab <%= "personalized".equals(feedMode) ? "is-active" : "" %>" href="${pageContext.request.contextPath}/member/home?mode=personalized">Home</a>
                    <a class="feed-tab <%= "explore".equals(feedMode) ? "is-active" : "" %>" href="${pageContext.request.contextPath}/member/home?mode=explore">Explore</a>
                    <a class="feed-tab" href="${pageContext.request.contextPath}/community">Communities</a>
                    <a class="feed-tab" href="${pageContext.request.contextPath}/member/my-posts">My posts</a>
                </div>

                <div class="social-feed">
                    <% for (Post post : feedPosts) { %>
                        <%
                        String content = post.getContent() != null ? post.getContent() : "";
                        String preview = content.length() > 320 ? content.substring(0, 320) + "..." : content;
                        %>
                        <article class="feed-post">
                            <header class="feed-post__header">
                                <a class="feed-post__avatar" href="${pageContext.request.contextPath}/community?slug=<%= post.getCommunitySlug() %>">
                                    <span><%= post.getCommunityName() != null && !post.getCommunityName().isEmpty() ? post.getCommunityName().substring(0, 1).toUpperCase() : "S" %></span>
                                </a>
                                <div>
                                    <h2><a href="${pageContext.request.contextPath}/post?id=<%= post.getPostId() %>"><%= post.getTitle() %></a></h2>
                                    <p>
                                        <a href="${pageContext.request.contextPath}/community?slug=<%= post.getCommunitySlug() %>"><%= post.getCommunityName() %></a>
                                        <span>by <%= post.getAuthorUsername() %></span>
                                    </p>
                                </div>
                            </header>

                            <% if (post.hasImage()) { %>
                                <a class="feed-post__media" href="${pageContext.request.contextPath}/post?id=<%= post.getPostId() %>">
                                    <img
                                        src="${pageContext.request.contextPath}<%= post.getImageUrl() %>"
                                        alt="<%= post.getImageAltText() != null ? post.getImageAltText() : post.getTitle() %>"
                                    >
                                </a>
                            <% } %>

                            <div class="feed-post__body">
                                <p><%= preview %></p>
                            </div>

                            <footer class="feed-post__actions">
                                <span><span class="material-symbols-outlined">thumb_up</span><%= post.getUpvotes() - post.getDownvotes() %></span>
                                <span><span class="material-symbols-outlined">chat_bubble</span><%= post.getCommentCount() %></span>
                                <span><span class="material-symbols-outlined">visibility</span><%= post.getViewCount() %></span>
                                <a href="${pageContext.request.contextPath}/post?id=<%= post.getPostId() %>">Open discussion</a>
                            </footer>
                        </article>
                    <% } %>
                </div>
            <% } else { %>
                <section class="empty-state">
                    <p class="eyebrow">No feed yet</p>
                    <h2>No approved posts are available.</h2>
                    <p>Once moderators approve member posts, they will appear here in the home feed.</p>
                </section>
            <% } %>
        </div>

        <aside class="feed-aside">
            <section class="section-card">
                <p class="eyebrow">Profile</p>
                <h2><%= currentUser != null ? currentUser.getDisplayName() : "Member" %></h2>
                <p class="muted">Your dashboard now lives in the profile section, with personal stats and account details together.</p>
                <a class="button button--ghost" href="${pageContext.request.contextPath}/member/profile">Open profile dashboard</a>
            </section>

            <section class="section-card">
                <div class="section-header">
                    <div>
                        <p class="eyebrow">Suggested</p>
                        <h2>Communities</h2>
                    </div>
                </div>
                <div class="stack-list">
                    <% if (suggestedCommunities != null && !suggestedCommunities.isEmpty()) { %>
                        <% for (Community community : suggestedCommunities) { %>
                            <a class="list-row" href="${pageContext.request.contextPath}/community?slug=<%= community.getSlug() %>">
                                <div>
                                    <strong><%= community.getName() %></strong>
                                    <p class="muted"><%= community.getMemberCount() %> members</p>
                                </div>
                                <span class="material-symbols-outlined">arrow_forward</span>
                            </a>
                        <% } %>
                    <% } else { %>
                        <div class="empty-state empty-state--soft">
                            <h3>No suggestions yet</h3>
                            <p>Communities will appear here after they are created.</p>
                        </div>
                    <% } %>
                </div>
            </section>
        </aside>
    </section>
</main>
<%@ include file="../common/footer.jsp" %>
