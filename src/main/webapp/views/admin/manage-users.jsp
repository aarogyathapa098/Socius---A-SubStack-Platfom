<%@ page import="java.util.List" %>
<%@ page import="model.User" %>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>
<%
List<User> allUsers = (List<User>) request.getAttribute("allUsers");
%>
<main class="content-shell">
    <section class="section-header">
        <div>
            <p class="eyebrow">Manage Users</p>
            <h1>Roles, penalties, and account access.</h1>
        </div>
    </section>

    <section class="section-card">
        <table class="data-table">
            <thead>
                <tr>
                    <th>User</th>
                    <th>Email</th>
                    <th>Role</th>
                    <th>Penalty</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
                <% if (allUsers != null && !allUsers.isEmpty()) { %>
                    <% for (User user : allUsers) { %>
                        <tr>
                            <td><strong><%= user.getUsername() %></strong></td>
                            <td><%= user.getEmail() %></td>
                            <td><span class="badge <%= "admin".equals(user.getRole()) ? "badge--ink" : ("moderator".equals(user.getRole()) ? "badge--mulberry" : "badge--sage") %>"><%= user.getRole() %></span></td>
                            <td><%= user.getPenaltyPoints() %></td>
                            <td><%= user.isActive() ? "Active" : "Inactive" %></td>
                        </tr>
                    <% } %>
                <% } else { %>
                    <tr>
                        <td colspan="5">No users found.</td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </section>
</main>
<%@ include file="../common/footer.jsp" %>
