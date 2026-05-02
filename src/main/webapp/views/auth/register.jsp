<%@ include file="../common/header.jsp" %>
<main class="auth-shell">
    <section class="auth-card">
        <div class="auth-card__intro">
            <p class="eyebrow">Create Account</p>
            <h1>Join a calmer digital space.</h1>
            <p class="muted">Create an account to join communities, publish thoughtful posts, and follow moderator feedback in one calm workspace.</p>
        </div>
        <%
        String registerError = (String) request.getAttribute("registerError");
        String submittedUsername = (String) request.getAttribute("submittedUsername");
        String submittedEmail = (String) request.getAttribute("submittedEmail");
        String submittedPhoneNumber = (String) request.getAttribute("submittedPhoneNumber");
        if (registerError != null) {
        %>
            <div class="flash flash--warning"><%= registerError %></div>
        <% } %>
        <form class="stack-form" action="${pageContext.request.contextPath}/register" method="post">
            <label for="regUsername">Username</label>
            <input id="regUsername" name="username" type="text" placeholder="3-30 characters, letters, numbers, underscore" value="<%= submittedUsername != null ? submittedUsername : "" %>" required>

            <label for="regEmail">Email</label>
            <input id="regEmail" name="email" type="email" placeholder="name@example.com" value="<%= submittedEmail != null ? submittedEmail : "" %>" required>

            <label for="regPhone">Phone number</label>
            <input id="regPhone" name="phoneNumber" type="text" placeholder="Unique contact number" value="<%= submittedPhoneNumber != null ? submittedPhoneNumber : "" %>" required>

            <label for="regPassword">Password</label>
            <input id="regPassword" name="password" type="password" placeholder="At least 8 chars, uppercase, number, special char" required>

            <label for="regConfirmPassword">Confirm password</label>
            <input id="regConfirmPassword" name="confirmPassword" type="password" placeholder="Repeat password" required>

            <div class="form-actions">
                <button class="button button--primary" type="submit">Create account</button>
            </div>
        </form>
    </section>
</main>
<%@ include file="../common/footer.jsp" %>
