<%@ include file="../common/header.jsp" %>
<%
String passwordError = (String) request.getAttribute("passwordError");
String submittedUsernameOrEmail = (String) request.getAttribute("submittedUsernameOrEmail");
%>
<main class="auth-shell">
    <section class="auth-card">
        <div class="auth-card__intro">
            <p class="eyebrow">Change Password</p>
            <h1>Set a new password.</h1>
            <p class="muted">Enter your username or email and choose a new password for your account.</p>
        </div>

        <% if (passwordError != null) { %>
            <div class="flash flash--warning"><%= passwordError %></div>
        <% } %>

        <form class="stack-form" action="${pageContext.request.contextPath}/change-password" method="post">
            <label for="resetAccount">Username or Email</label>
            <input
                id="resetAccount"
                name="usernameOrEmail"
                type="text"
                placeholder="e.g. ava_clarke or ava@socius.app"
                value="<%= submittedUsernameOrEmail != null ? submittedUsernameOrEmail : "" %>"
                required
            >

            <label for="newPassword">New password</label>
            <input
                id="newPassword"
                name="newPassword"
                type="password"
                placeholder="At least 8 chars, uppercase, number, special char"
                required
            >

            <label for="confirmPassword">Confirm new password</label>
            <input
                id="confirmPassword"
                name="confirmPassword"
                type="password"
                placeholder="Repeat new password"
                required
            >

            <div class="form-actions form-actions--spread">
                <a class="text-link" href="${pageContext.request.contextPath}/login">Back to sign in</a>
                <button class="button button--primary" type="submit">Change password</button>
            </div>
        </form>
    </section>
</main>
<%@ include file="../common/footer.jsp" %>
