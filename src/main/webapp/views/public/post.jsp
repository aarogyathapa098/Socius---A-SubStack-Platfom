<%@ page import="java.util.List" %>
<%@ page import="model.Comment" %>
<%@ page import="model.Post" %>
<%@ page import="model.Vote" %>
<%@ include file="../common/header.jsp" %>
<%
Post post = (Post) request.getAttribute("post");
List<Comment> comments = (List<Comment>) request.getAttribute("comments");
Boolean postNotFound = (Boolean) request.getAttribute("postNotFound");
Vote currentVote = (Vote) request.getAttribute("currentVote");
String currentVoteType = currentVote != null ? currentVote.getVoteType() : "";
%>
<main class="content-shell content-shell--public">
    <% if (postNotFound != null && postNotFound.booleanValue()) { %>
        <section class="section-card empty-state">
            <p class="eyebrow">Post</p>
            <h1>Post not found</h1>
            <p>The requested post does not exist or is not publicly approved yet.</p>
            <a class="button button--primary" href="${pageContext.request.contextPath}/community">Browse communities</a>
        </section>
    <% } else if (post != null) { %>
        <article class="section-card post-layout">
            <div class="post-layout__header">
                <p class="eyebrow">Post</p>
                <h1><%= post.getTitle() %></h1>
                <div class="entity-meta">
                    <a class="text-link" href="${pageContext.request.contextPath}/community?slug=<%= post.getCommunitySlug() %>"><%= post.getCommunityName() %></a>
                    <span><%= post.getAuthorUsername() %></span>
                    <span><%= post.getCommentCount() %> comments</span>
                    <span><%= post.getUpvotes() - post.getDownvotes() %> score</span>
                    <span><%= post.getViewCount() %> views</span>
                </div>
                <% if (currentUser != null && currentUser.getUserId() == post.getAuthorId()) { %>
                    <div class="form-actions">
                        <a class="button button--ghost" href="${pageContext.request.contextPath}/member/edit-post?id=<%= post.getPostId() %>">Edit</a>
                    </div>
                <% } %>
            </div>

            <% if (post.hasImage()) { %>
                <figure class="post-media">
                    <img
                        src="${pageContext.request.contextPath}<%= post.getImageUrl() %>"
                        alt="<%= post.getImageAltText() != null ? post.getImageAltText() : post.getTitle() %>"
                    >
                </figure>
            <% } %>

            <div class="rich-copy rich-copy--plain">
                <p><%= post.getContent() %></p>
            </div>

            <% if (currentUser != null) { %>
                <form class="vote-panel" action="${pageContext.request.contextPath}/post" method="post" aria-label="Vote on this post">
                    <input type="hidden" name="action" value="vote">
                    <input type="hidden" name="postId" value="<%= post.getPostId() %>">
                    <button class="vote-button <%= "up".equals(currentVoteType) ? "is-active" : "" %>" type="submit" name="voteType" value="up">
                        <span class="material-symbols-outlined">thumb_up</span>
                        <span><%= post.getUpvotes() %></span>
                    </button>
                    <button class="vote-button <%= "down".equals(currentVoteType) ? "is-active" : "" %>" type="submit" name="voteType" value="down">
                        <span class="material-symbols-outlined">thumb_down</span>
                        <span><%= post.getDownvotes() %></span>
                    </button>
                </form>
            <% } %>
        </article>

        <section class="public-grid public-grid--comments">
            <section class="section-card">
                <div class="section-header">
                    <div>
                        <p class="eyebrow">Discussion</p>
                        <h2>Published responses.</h2>
                    </div>
                </div>

                <% if (currentUser != null) { %>
                    <form class="stack-form comment-form" action="${pageContext.request.contextPath}/post" method="post">
                        <input type="hidden" name="action" value="comment">
                        <input type="hidden" name="postId" value="<%= post.getPostId() %>">
                        <label for="commentContent">Add a comment</label>
                        <textarea id="commentContent" name="content" rows="4" placeholder="Share a thoughtful reply." required></textarea>
                        <div class="form-actions">
                            <button class="button button--primary" type="submit">Post comment</button>
                        </div>
                    </form>
                <% } %>

                <% if (comments != null && !comments.isEmpty()) { %>
                    <div class="comment-list">
                        <% for (Comment comment : comments) { %>
                            <article class="comment-card">
                                <div class="comment-card__meta">
                                    <strong><%= comment.getAuthorUsername() %></strong>
                                    <span class="muted"><%= comment.getUpvotes() %> upvotes</span>
                                </div>
                                <p><%= comment.isRemoved() ? "This comment has been removed." : comment.getContent() %></p>
                                <% if (currentUser != null && !comment.isRemoved()) { %>
                                    <form class="mini-action-form" action="${pageContext.request.contextPath}/post" method="post">
                                        <input type="hidden" name="action" value="report">
                                        <input type="hidden" name="postId" value="<%= post.getPostId() %>">
                                        <input type="hidden" name="commentId" value="<%= comment.getCommentId() %>">
                                        <input type="hidden" name="reason" value="Comment needs moderator review">
                                        <button class="text-link" type="submit">Report comment</button>
                                    </form>
                                <% } %>

                                <% if (comment.getReplies() != null && !comment.getReplies().isEmpty()) { %>
                                    <div class="comment-replies">
                                        <% for (Comment reply : comment.getReplies()) { %>
                                            <article class="comment-card comment-card--reply">
                                                <div class="comment-card__meta">
                                                    <strong><%= reply.getAuthorUsername() %></strong>
                                                    <span class="muted"><%= reply.getUpvotes() %> upvotes</span>
                                                </div>
                                                <p><%= reply.isRemoved() ? "This reply has been removed." : reply.getContent() %></p>
                                            </article>
                                        <% } %>
                                    </div>
                                <% } %>
                            </article>
                        <% } %>
                    </div>
                <% } else { %>
                    <div class="empty-state">
                        <h3>No comments yet</h3>
                        <p>This post is published, but no one has replied to it yet.</p>
                    </div>
                <% } %>
            </section>

            <aside class="section-card">
                <p class="eyebrow">Participation</p>
                <h2>Join the thread</h2>
                <% if (currentUser != null) { %>
                    <form class="stack-form report-form" action="${pageContext.request.contextPath}/post" method="post">
                        <input type="hidden" name="action" value="report">
                        <input type="hidden" name="postId" value="<%= post.getPostId() %>">
                        <label for="reportReason">Report this post</label>
                        <select id="reportReason" name="reason">
                            <option value="Spam or misleading content">Spam or misleading content</option>
                            <option value="Harassment or unsafe behavior">Harassment or unsafe behavior</option>
                            <option value="Off-topic for this community">Off-topic for this community</option>
                            <option value="Needs moderator review">Needs moderator review</option>
                        </select>
                        <button class="button button--ghost" type="submit">Send report</button>
                    </form>
                    <a class="button button--ghost" href="${pageContext.request.contextPath}/member/home">Return to your workspace</a>
                <% } else { %>
                    <a class="button button--ghost" href="${pageContext.request.contextPath}/login">Sign in to participate</a>
                <% } %>
            </aside>
        </section>
    <% } %>
</main>
<%@ include file="../common/footer.jsp" %>
