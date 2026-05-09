<%@ page import="java.util.List" %>
<%@ page import="model.Post" %>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>
<%
List<Post> feedPosts = (List<Post>) request.getAttribute("feedPosts");
String feedMode = (String) request.getAttribute("feedMode");
if (feedMode == null) {
    feedMode = "personalized";
}
%>
<main class="content-shell content-shell--home">
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
                                        width="1200"
                                        height="675"
                                        loading="lazy"
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

    </section>
</main>
<%@ include file="../common/footer.jsp" %>
