<%@ page import="java.util.List" %>
<%@ page import="model.User" %>
<%!
private String h(String value) {
    if (value == null) {
        return "";
    }

    return value
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;");
}
%>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>
<%
List<User> allUsers = (List<User>) request.getAttribute("allUsers");
User editUser = (User) request.getAttribute("editUser");
String userError = (String) request.getAttribute("userError");
%>
<main class="content-shell">
    <section class="section-header">
        <div>
            <p class="eyebrow">Manage Users</p>
            <h1>Roles, penalties, and account access.</h1>
        </div>
    </section>

    <% if (userError != null) { %>
        <div class="flash flash--warning"><%= h(userError) %></div>
    <% } %>

    <% if (editUser != null) { %>
        <%
        boolean editSelfAccount = currentUser != null && currentUser.getUserId() == editUser.getUserId();
        boolean editDisplayActive = editUser.isActive() && !editUser.isGloballyBanned();
        %>
        <section class="section-card">
            <div class="section-header">
                <div>
                    <p class="eyebrow">Edit User</p>
                    <h2><%= h(editUser.getUsername()) %></h2>
                </div>
                <a class="button button--ghost" href="${pageContext.request.contextPath}/admin/manage-users">Cancel</a>
            </div>

            <form action="${pageContext.request.contextPath}/admin/manage-users" method="post" class="stack-form">
                <input type="hidden" name="userId" value="<%= editUser.getUserId() %>">

                <label for="editUsername">Username</label>
                <input id="editUsername" name="username" type="text" value="<%= h(editUser.getUsername()) %>" maxlength="30" required>

                <label for="editDisplayName">Name</label>
                <input id="editDisplayName" name="displayName" type="text" value="<%= h(editUser.getDisplayName()) %>" maxlength="100" required>

                <label for="editEmail">Email</label>
                <input id="editEmail" name="email" type="email" value="<%= h(editUser.getEmail()) %>" required>

                <label for="editPhone">Phone number</label>
                <input id="editPhone" name="phoneNumber" type="text" value="<%= h(editUser.getPhoneNumber()) %>" required>

                <label for="editRole">Role</label>
                <select id="editRole" name="role" <%= editSelfAccount ? "disabled" : "" %>>
                    <option value="member" <%= "member".equals(editUser.getRole()) ? "selected" : "" %>>Member</option>
                    <option value="moderator" <%= "moderator".equals(editUser.getRole()) ? "selected" : "" %>>Moderator</option>
                    <option value="admin" <%= "admin".equals(editUser.getRole()) ? "selected" : "" %>>Admin</option>
                </select>
                <% if (editSelfAccount) { %>
                    <input name="role" type="hidden" value="admin">
                <% } %>

                <label for="editPenalty">Penalty points</label>
                <input id="editPenalty" name="penaltyPoints" type="number" min="0" value="<%= editUser.getPenaltyPoints() %>">

                <label for="editStatus">Status</label>
                <select id="editStatus" name="accountStatus" <%= editSelfAccount ? "disabled" : "" %>>
                    <option value="active" <%= editDisplayActive ? "selected" : "" %>>Active</option>
                    <option value="inactive" <%= !editDisplayActive ? "selected" : "" %>>Inactive</option>
                </select>
                <% if (editSelfAccount) { %>
                    <input name="accountStatus" type="hidden" value="active">
                <% } %>

                <label for="editBan">Ban</label>
                <select id="editBan" name="globallyBanned" <%= editSelfAccount ? "disabled" : "" %>>
                    <option value="false" <%= !editUser.isGloballyBanned() ? "selected" : "" %>>Not banned</option>
                    <option value="true" <%= editUser.isGloballyBanned() ? "selected" : "" %>>Banned</option>
                </select>
                <% if (editSelfAccount) { %>
                    <input name="globallyBanned" type="hidden" value="false">
                <% } %>

                <div>
                    <button class="button button--primary" type="submit" name="action" value="save">Save User</button>
                </div>
            </form>
        </section>
    <% } %>

    <section class="section-card">
        <table class="data-table">
            <thead>
                <tr>
                    <th>User</th>
                    <th>Contact</th>
                    <th>Role</th>
                    <th>Penalty</th>
                    <th>Status</th>
                    <th>Ban</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <% if (allUsers != null && !allUsers.isEmpty()) { %>
                    <% for (User user : allUsers) { %>
                        <%
                        boolean selfAccount = currentUser != null && currentUser.getUserId() == user.getUserId();
                        boolean displayActive = user.isActive() && !user.isGloballyBanned();
                        boolean selectedForEdit = editUser != null && editUser.getUserId() == user.getUserId();
                        %>
                        <tr>
                            <td>
                                <strong><%= h(user.getUsername()) %></strong>
                                <% if (selfAccount) { %>
                                    <span class="badge badge--ink table-badge">You</span>
                                <% } %>
                                <br>
                                <%= h(user.getDisplayName()) %>
                            </td>
                            <td>
                                <%= h(user.getEmail()) %>
                                <br>
                                <%= h(user.getPhoneNumber()) %>
                            </td>
                            <td>
                                <span class="badge <%= "admin".equals(user.getRole()) ? "badge--ink" : ("moderator".equals(user.getRole()) ? "badge--mulberry" : "badge--sage") %>">
                                    <%= h(user.getRole()) %>
                                </span>
                            </td>
                            <td><%= user.getPenaltyPoints() %></td>
                            <td>
                                <span class="badge <%= displayActive ? "badge--sage" : "badge--rose" %>">
                                    <%= displayActive ? "Active" : "Inactive" %>
                                </span>
                            </td>
                            <td><%= user.isGloballyBanned() ? "Banned" : "Not banned" %></td>
                            <td>
                                <% if (selectedForEdit) { %>
                                    <span class="badge badge--ink">Editing</span>
                                <% } else { %>
                                    <a class="button button--ghost" href="${pageContext.request.contextPath}/admin/manage-users?editUserId=<%= user.getUserId() %>">Edit</a>
                                <% } %>
                            </td>
                        </tr>
                    <% } %>
                <% } else { %>
                    <tr>
                        <td colspan="7">No users found.</td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </section>
</main>
<%@ include file="../common/footer.jsp" %>
