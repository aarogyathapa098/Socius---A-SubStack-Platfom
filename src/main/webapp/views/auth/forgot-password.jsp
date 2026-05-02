<%@ include file="../common/header.jsp" %>
<main class="auth-shell">
    <section class="auth-card">
        <div class="auth-card__intro">
            <p class="eyebrow">Password Reset</p>
            <h1>Recover your account securely.</h1>
            <p class="muted">The schema and utilities are ready for token-based reset wiring.</p>
        </div>
        <form class="stack-form" action="${pageContext.request.contextPath}/forgot-password" method="post">
            <label for="resetEmail">Email address</label>
            <input id="resetEmail" name="email" type="email" placeholder="Enter your account email">

            <div class="form-actions">
                <button class="button button--primary" type="submit">Send reset link</button>
            </div>
        </form>
    </section>
</main>
<%@ include file="../common/footer.jsp" %>
