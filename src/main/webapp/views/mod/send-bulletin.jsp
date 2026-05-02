<%@ page import="java.util.List" %>
<%@ page import="model.Bulletin" %>
<%@ page import="model.Community" %>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>
<%
List<Community> communities = (List<Community>) request.getAttribute("communities");
List<Bulletin> recentBulletins = (List<Bulletin>) request.getAttribute("recentBulletins");
String bulletinError = (String) request.getAttribute("bulletinError");
Integer submittedBulletinCommunityId = (Integer) request.getAttribute("submittedBulletinCommunityId");
String submittedBulletinSubject = (String) request.getAttribute("submittedBulletinSubject");
String submittedBulletinBody = (String) request.getAttribute("submittedBulletinBody");
%>
<main class="content-shell">
    <section class="split-section">
        <div class="section-card">
            <p class="eyebrow">Community Bulletin</p>
            <h1>Send an update to members.</h1>
            <% if (bulletinError != null) { %>
                <div class="flash flash--warning"><%= bulletinError %></div>
            <% } %>
            <form class="stack-form" action="${pageContext.request.contextPath}/moderator/send-bulletin" method="post">
                <label for="bulletinCommunity">Community</label>
                <select id="bulletinCommunity" name="communityId" required>
                    <option value="">Choose a community</option>
                    <% if (communities != null) { %>
                        <% for (Community community : communities) { %>
                            <option value="<%= community.getCommunityId() %>" <%= submittedBulletinCommunityId != null && submittedBulletinCommunityId.intValue() == community.getCommunityId() ? "selected" : "" %>>
                                <%= community.getName() %>
                            </option>
                        <% } %>
                    <% } %>
                </select>

                <label for="bulletinSubject">Subject</label>
                <input id="bulletinSubject" name="subject" type="text" placeholder="Monthly moderation update" value="<%= submittedBulletinSubject != null ? submittedBulletinSubject : "" %>" required>

                <label for="bulletinBody">Body</label>
                <textarea id="bulletinBody" name="body" rows="8" placeholder="Share important updates, highlights, and reminders." required><%= submittedBulletinBody != null ? submittedBulletinBody : "" %></textarea>

                <div class="form-actions">
                    <button class="button button--primary" type="submit">Send bulletin</button>
                </div>
            </form>
        </div>

        <div class="section-card">
            <p class="eyebrow">Recent Bulletins</p>
            <div class="preview-box" id="newsletterPreview">
                <%= submittedBulletinBody != null && !submittedBulletinBody.trim().isEmpty()
                    ? submittedBulletinBody.replace("\n", "<br>")
                    : "Bulletin preview will appear here as the user types." %>
            </div>
            <div class="text-card-list text-card-list--tight">
                <% if (recentBulletins != null && !recentBulletins.isEmpty()) { %>
                    <% for (Bulletin bulletin : recentBulletins) { %>
                        <article class="text-card text-card--compact">
                            <div class="text-card__topline">
                                <span class="badge badge--ink"><%= bulletin.getCommunityName() %></span>
                                <span class="muted"><%= bulletin.getRecipientCount() %> recipients</span>
                            </div>
                            <h3><%= bulletin.getSubject() %></h3>
                            <p><%= bulletin.getBody() %></p>
                        </article>
                    <% } %>
                <% } else { %>
                    <div class="empty-state empty-state--soft">
                        <h3>No bulletins yet</h3>
                        <p>Send the first bulletin from this page.</p>
                    </div>
                <% } %>
            </div>
        </div>
    </section>
</main>
<%@ include file="../common/footer.jsp" %>
