<%
Integer moderatorPendingCount = (Integer) request.getAttribute("moderatorPendingCount");
Integer moderatorReportCount = (Integer) request.getAttribute("moderatorReportCount");
Integer moderatorBanCount = (Integer) request.getAttribute("moderatorBanCount");
Integer moderatorBulletinCount = (Integer) request.getAttribute("moderatorBulletinCount");
%>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>
<main class="content-shell">
    <section class="section-card section-card--hero-copy">
        <p class="eyebrow">Moderator Dashboard</p>
        <h1>Review, guide, and protect the conversation.</h1>
        <p class="lead-sm">Use this workspace to review pending posts, respond to reports, manage bans, and keep each community constructive.</p>
    </section>

    <section class="metric-grid">
        <div class="metric-card"><strong><%= moderatorPendingCount != null ? moderatorPendingCount.intValue() : 0 %></strong><span>Pending reviews</span></div>
        <div class="metric-card"><strong><%= moderatorReportCount != null ? moderatorReportCount.intValue() : 0 %></strong><span>Open reports</span></div>
        <div class="metric-card"><strong><%= moderatorBanCount != null ? moderatorBanCount.intValue() : 0 %></strong><span>Active bans</span></div>
        <div class="metric-card"><strong><%= moderatorBulletinCount != null ? moderatorBulletinCount.intValue() : 0 %></strong><span>Bulletins sent</span></div>
    </section>

    <section class="split-section">
        <div class="section-card">
            <p class="eyebrow">Quick Actions</p>
            <div class="stack-list">
                <a class="list-row" href="${pageContext.request.contextPath}/moderator/approval-queue"><strong>Open approval queue</strong><span><%= moderatorPendingCount != null ? moderatorPendingCount.intValue() : 0 %> items</span></a>
                <a class="list-row" href="${pageContext.request.contextPath}/moderator/reported-posts"><strong>Review reported posts</strong><span><%= moderatorReportCount != null ? moderatorReportCount.intValue() : 0 %> open</span></a>
                <a class="list-row" href="${pageContext.request.contextPath}/moderator/banned-members"><strong>Manage banned members</strong><span><%= moderatorBanCount != null ? moderatorBanCount.intValue() : 0 %> active</span></a>
                <a class="list-row" href="${pageContext.request.contextPath}/moderator/send-bulletin"><strong>Send community bulletin</strong><span><%= moderatorBulletinCount != null ? moderatorBulletinCount.intValue() : 0 %> sent</span></a>
            </div>
        </div>
        <div class="section-card">
            <p class="eyebrow">Moderation Notes</p>
            <ul class="bullet-list">
                <li>Rejected posts should store clear reasons for author improvement.</li>
                <li>Penalty points help moderators track repeated harmful behaviour fairly.</li>
                <li>Bans should remove access to posting in the affected community.</li>
            </ul>
        </div>
    </section>
</main>
<%@ include file="../common/footer.jsp" %>
