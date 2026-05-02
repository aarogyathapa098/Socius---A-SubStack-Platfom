<%@ page import="java.util.List" %>
<%@ page import="model.Ban" %>
<%@ page import="model.Community" %>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>
<%
List<Ban> allBans = (List<Ban>) request.getAttribute("allBans");
List<Community> communities = (List<Community>) request.getAttribute("communities");
String banError = (String) request.getAttribute("banError");
String submittedBanUsername = (String) request.getAttribute("submittedBanUsername");
Integer submittedBanCommunityId = (Integer) request.getAttribute("submittedBanCommunityId");
String submittedBanReason = (String) request.getAttribute("submittedBanReason");
%>
<main class="content-shell">
    <section class="split-section">
        <div class="section-card">
            <p class="eyebrow">Apply Community Ban</p>
            <% if (banError != null) { %>
                <div class="flash flash--warning"><%= banError %></div>
            <% } %>
            <form class="stack-form" action="${pageContext.request.contextPath}/moderator/banned-members" method="post">
                <label for="banUser">Username</label>
                <input id="banUser" name="username" type="text" placeholder="Search username" value="<%= submittedBanUsername != null ? submittedBanUsername : "" %>" required>

                <label for="banCommunity">Community</label>
                <select id="banCommunity" name="communityId" required>
                    <option value="">Choose a community</option>
                    <% if (communities != null) { %>
                        <% for (Community community : communities) { %>
                            <option value="<%= community.getCommunityId() %>" <%= submittedBanCommunityId != null && submittedBanCommunityId.intValue() == community.getCommunityId() ? "selected" : "" %>>
                                <%= community.getName() %>
                            </option>
                        <% } %>
                    <% } %>
                </select>

                <label for="banReason">Reason</label>
                <textarea id="banReason" name="reason" rows="4" placeholder="Explain the moderation decision clearly." required><%= submittedBanReason != null ? submittedBanReason : "" %></textarea>

                <div class="form-actions">
                    <button class="button button--primary" type="submit">Apply ban</button>
                </div>
            </form>
        </div>

        <div class="section-card">
            <p class="eyebrow">Active Bans</p>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>User</th>
                        <th>Community</th>
                        <th>Reason</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (allBans != null && !allBans.isEmpty()) { %>
                        <% for (Ban ban : allBans) { %>
                            <tr>
                                <td><%= ban.getUsername() %></td>
                                <td><%= ban.getCommunityName() != null ? ban.getCommunityName() : "Global" %></td>
                                <td><%= ban.getReason() %></td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/moderator/banned-members" method="post" class="inline-form">
                                        <input type="hidden" name="action" value="remove">
                                        <input type="hidden" name="banId" value="<%= ban.getBanId() %>">
                                        <button class="button button--ghost" type="submit">Unban</button>
                                    </form>
                                </td>
                            </tr>
                        <% } %>
                    <% } else { %>
                        <tr>
                            <td colspan="4">No active bans found.</td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </section>
</main>
<%@ include file="../common/footer.jsp" %>
