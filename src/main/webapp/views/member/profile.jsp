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
String profileError = (String) request.getAttribute("profileError");
String username = currentUser != null && currentUser.getUsername() != null ? currentUser.getUsername() : "";
String displayName = currentUser != null && currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "";
String phoneNumber = currentUser != null && currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "";
String bio = currentUser != null && currentUser.getBio() != null ? currentUser.getBio() : "";
%>
<main class="content-shell">
    <section class="section-card section-card--hero-copy">
        <p class="eyebrow">Profile Dashboard</p>
        <h1>Your profile and activity dashboard.</h1>
        <p class="lead-sm">Your personal dashboard now lives with your profile, so account details and activity health are in one place.</p>
    </section>

    <section class="metric-grid">
        <div class="metric-card"><strong><%= memberPostCount != null ? memberPostCount.intValue() : 0 %></strong><span>Your posts</span></div>
        <div class="metric-card"><strong><%= memberPendingCount != null ? memberPendingCount.intValue() : 0 %></strong><span>Pending review</span></div>
        <div class="metric-card"><strong><%= memberCommunityCount != null ? memberCommunityCount.intValue() : 0 %></strong><span>Joined communities</span></div>
        <div class="metric-card"><strong><%= currentUser != null ? currentUser.getPenaltyPoints() : 0 %></strong><span>Penalty points</span></div>
    </section>

    <section class="split-section">
        <div class="section-card">
            <p class="eyebrow">Profile</p>
            <h2>Update your account details.</h2>
            <% if (profileError != null) { %>
                <div class="flash flash--warning"><%= profileError %></div>
            <% } %>
            <form class="stack-form" action="${pageContext.request.contextPath}/member/profile" method="post">
                <label for="profileUsername">Username</label>
                <input id="profileUsername" type="text" value="<%= username %>" readonly>

                <label for="profileName">Display name</label>
                <input id="profileName" name="displayName" type="text" value="<%= displayName %>" required maxlength="100">

                <label for="profilePhone">Phone number</label>
                <input id="profilePhone" name="phoneNumber" type="text" value="<%= phoneNumber %>">

                <label for="profileBio">Bio</label>
                <textarea id="profileBio" name="bio" rows="5" maxlength="1000"><%= bio %></textarea>

                <div class="form-actions">
                    <button class="button button--primary" type="submit">Save profile</button>
                </div>
            </form>
        </div>

        <div class="section-card">
            <div class="section-header">
                <div>
                    <p class="eyebrow">Recent Activity</p>
                    <h2>Your publishing queue.</h2>
                </div>
                <a class="text-link" href="${pageContext.request.contextPath}/member/my-posts">See all</a>
            </div>
            <div class="stack-list">
                <% if (memberPosts != null && !memberPosts.isEmpty()) { %>
                    <% for (Post post : memberPosts) { %>
                        <div class="list-row">
                            <div>
                                <strong><%= post.getTitle() %></strong>
                                <p class="muted"><%= post.getCommunityName() %></p>
                            </div>
                            <span class="badge <%= "approved".equals(post.getStatus()) ? "badge--sage" : ("rejected".equals(post.getStatus()) ? "badge--rose" : "badge--amber") %>">
                                <%= post.getStatus() %>
                            </span>
                        </div>
                    <% } %>
                <% } else { %>
                    <div class="empty-state empty-state--soft">
                        <h3>No posts yet</h3>
                        <p>Create a post from Home to start building your activity.</p>
                    </div>
                <% } %>
            </div>
        </div>
    </section>

    <section class="section-card">
        <p class="eyebrow">Account Security</p>
        <ul class="bullet-list">
            <li>Password reset tokens are supported by the starter schema.</li>
            <li>Failed login attempts can trigger account lockout.</li>
            <li>Penalty points can be used to track moderation history.</li>
        </ul>
    </section>
</main>
<%@ include file="../common/footer.jsp" %>
