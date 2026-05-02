<%@ page import="java.util.List" %>
<%@ page import="model.Community" %>
<%@ page import="model.CommunityModerator" %>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>
<%
List<Community> communities = (List<Community>) request.getAttribute("communities");
List<CommunityModerator> moderatorAssignments = (List<CommunityModerator>) request.getAttribute("moderatorAssignments");
String moderatorError = (String) request.getAttribute("moderatorError");
String submittedModeratorUsername = (String) request.getAttribute("submittedModeratorUsername");
Integer submittedModeratorCommunityId = (Integer) request.getAttribute("submittedModeratorCommunityId");
%>
<main class="content-shell">
    <section class="split-section">
        <div class="section-card">
            <p class="eyebrow">Assign Moderator</p>
            <% if (moderatorError != null) { %>
                <div class="flash flash--warning"><%= moderatorError %></div>
            <% } %>
            <form class="stack-form" action="${pageContext.request.contextPath}/admin/manage-moderators" method="post">
                <label for="modUser">Username</label>
                <input id="modUser" name="username" type="text" placeholder="ava_clarke" value="<%= submittedModeratorUsername != null ? submittedModeratorUsername : "" %>" required>

                <label for="modCommunity">Community</label>
                <select id="modCommunity" name="communityId" required>
                    <option value="">Choose a community</option>
                    <% if (communities != null) { %>
                        <% for (Community community : communities) { %>
                            <option value="<%= community.getCommunityId() %>" <%= submittedModeratorCommunityId != null && submittedModeratorCommunityId.intValue() == community.getCommunityId() ? "selected" : "" %>>
                                <%= community.getName() %>
                            </option>
                        <% } %>
                    <% } %>
                </select>

                <div class="form-actions">
                    <button class="button button--primary" type="submit">Assign role</button>
                </div>
            </form>
        </div>

        <div class="section-card">
            <p class="eyebrow">Current Moderators</p>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>User</th>
                        <th>Community</th>
                        <th>Assigned</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (moderatorAssignments != null && !moderatorAssignments.isEmpty()) { %>
                        <% for (CommunityModerator assignment : moderatorAssignments) { %>
                            <tr>
                                <td><%= assignment.getUsername() %></td>
                                <td><%= assignment.getCommunityName() %></td>
                                <td><%= assignment.getAssignedAt() %></td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/admin/manage-moderators" method="post" class="inline-form">
                                        <input type="hidden" name="action" value="remove">
                                        <input type="hidden" name="moderatorId" value="<%= assignment.getModeratorId() %>">
                                        <button class="button button--ghost" type="submit">Remove</button>
                                    </form>
                                </td>
                            </tr>
                        <% } %>
                    <% } else { %>
                        <tr>
                            <td colspan="4">No moderator assignments found.</td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </section>
</main>
<%@ include file="../common/footer.jsp" %>
