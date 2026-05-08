<%@ include file="../common/header.jsp" %>
<%
String loginError = (String) request.getAttribute("loginError");
String submittedUsername = (String) request.getAttribute("submittedUsername");
Long lockRemainingSeconds = (Long) request.getAttribute("lockRemainingSeconds");
boolean accountLocked = lockRemainingSeconds != null && lockRemainingSeconds.longValue() > 0;
%>
<main class="auth-shell">
    <section class="auth-card">
        <div class="auth-card__intro">
            <p class="eyebrow">Welcome Back</p>
            <h1>Sign in to Socius.</h1>
            <p class="muted">Access your ethical community network. Use your registered username or email to continue.</p>
        </div>

        <% if (loginError != null) { %>
            <div class="flash flash--warning" <%= accountLocked ? "data-persistent=\"true\"" : "" %>>
                <%= loginError %>
                <% if (accountLocked) { %>
                    <span id="accountLockTimer" data-remaining-seconds="<%= lockRemainingSeconds.longValue() %>"></span>
                <% } %>
            </div>
        <% } %>

        <form class="stack-form" action="${pageContext.request.contextPath}/login" method="post">
            <label for="username">Username or Email</label>
            <input
                id="username"
                name="username"
                type="text"
                placeholder="e.g. ava_clarke"
                value="<%= submittedUsername != null ? submittedUsername : "" %>"
                required
            >

            <label for="password">Password</label>
            <input
                id="password"
                name="password"
                type="password"
                placeholder="Enter your password"
                required
            >

            <div class="form-actions form-actions--spread">
                <a class="text-link" href="${pageContext.request.contextPath}/forgot-password">Forgot password?</a>
                <button id="loginSubmitButton" class="button button--primary" type="submit" <%= accountLocked ? "disabled" : "" %>>Sign in</button>
            </div>
        </form>

        <div class="auth-card__footer">
            <p class="muted">New to Socius? <a href="${pageContext.request.contextPath}/register" class="text-link">Create an account</a></p>
        </div>
    </section>
</main>
<%@ include file="../common/footer.jsp" %>
