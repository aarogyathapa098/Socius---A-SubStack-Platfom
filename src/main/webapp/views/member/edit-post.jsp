<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>
<main class="content-shell">
    <section class="section-card">
        <p class="eyebrow">Edit Post</p>
        <h1>Revise and resubmit.</h1>
        <p class="lead-sm">Rejected posts should save the moderator reason and return to pending review when the member updates them.</p>
    </section>

    <section class="split-section">
        <div class="section-card">
            <form class="stack-form">
                <label for="editTitle">Title</label>
                <input id="editTitle" type="text" value="My first attempt at a volunteer rota guide">

                <label for="editBody">Content</label>
                <textarea id="editBody" rows="10">I want to rewrite this with clearer scheduling examples and better accessibility guidance.</textarea>

                <div class="form-actions">
                    <button class="button button--primary" type="submit">Resubmit</button>
                </div>
            </form>
        </div>
        <div class="section-card">
            <p class="eyebrow">Moderator feedback</p>
            <p class="muted">Please provide clearer examples and explain how volunteers can request cover without exposing personal circumstances.</p>
        </div>
    </section>
</main>
<%@ include file="../common/footer.jsp" %>
