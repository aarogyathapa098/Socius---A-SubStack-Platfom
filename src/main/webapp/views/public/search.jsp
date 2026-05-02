<%@ page import="java.util.List" %>
<%@ page import="model.Community" %>
<%@ page import="model.Post" %>
<%@ include file="../common/header.jsp" %>
<%
String query = (String) request.getAttribute("query");
List<Community> communities = (List<Community>) request.getAttribute("communities");
List<Post> posts = (List<Post>) request.getAttribute("posts");
boolean hasQuery = query != null && query.trim().length() >= 2;
%>
<main class="content-shell content-shell--public">
    <section class="section-card public-hero">
        <div class="public-hero__copy">
            <p class="eyebrow">Search</p>
            <h1><%= hasQuery ? "Results for \"" + query + "\"" : "Search posts and communities." %></h1>
            <p class="lead">
                <%= hasQuery
                    ? "Unified results include approved posts and public communities."
                    : "Start typing in the top search bar for partial suggestions, or submit a query for full results." %>
            </p>
        </div>
        <form class="topbar__search search-page-form" action="${pageContext.request.contextPath}/search" method="get" autocomplete="off">
            <span class="material-symbols-outlined">search</span>
            <input type="search" name="q" value="<%= query != null ? query : "" %>" placeholder="Search Socius" aria-label="Search Socius">
        </form>
    </section>

    <section class="public-grid">
        <section class="section-card">
            <div class="section-header">
                <div>
                    <p class="eyebrow">Posts</p>
                    <h2><%= hasQuery ? "Matching discussions." : "Trending discussions." %></h2>
                </div>
            </div>
            <% if (posts != null && !posts.isEmpty()) { %>
                <div class="text-card-list">
                    <% for (Post post : posts) { %>
                        <article class="text-card">
                            <div class="text-card__topline">
                                <span class="badge badge--ink"><%= post.getCommunityName() %></span>
                                <span class="muted"><%= post.getUpvotes() %> upvotes</span>
                            </div>
                            <h3><a href="${pageContext.request.contextPath}/post?id=<%= post.getPostId() %>"><%= post.getTitle() %></a></h3>
                            <p><%= post.getContent() != null && post.getContent().length() > 220 ? post.getContent().substring(0, 220) + "..." : post.getContent() %></p>
                            <div class="entity-meta">
                                <span><%= post.getAuthorUsername() %></span>
                                <span><%= post.getCommentCount() %> comments</span>
                            </div>
                        </article>
                    <% } %>
                </div>
            <% } else { %>
                <div class="empty-state empty-state--soft">
                    <h3>No posts found</h3>
                    <p>Try a broader search term.</p>
                </div>
            <% } %>
        </section>

        <aside class="section-card">
            <div class="section-header">
                <div>
                    <p class="eyebrow">Communities</p>
                    <h2><%= hasQuery ? "Matching spaces." : "Popular spaces." %></h2>
                </div>
            </div>
            <div class="stack-list">
                <% if (communities != null && !communities.isEmpty()) { %>
                    <% for (Community community : communities) { %>
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
                        <h3>No communities found</h3>
                        <p>Approved communities matching your search will appear here.</p>
                    </div>
                <% } %>
            </div>
        </aside>
    </section>
</main>
<%@ include file="../common/footer.jsp" %>
