<%
request.setAttribute("showSidebar", Boolean.FALSE);
String databaseErrorMessage = (String) request.getAttribute("databaseErrorMessage");
%>
<%@ include file="../common/header.jsp" %>
<main class="auth-shell">
    <section class="auth-card">
        <div class="auth-card__intro">
            <p class="eyebrow">Database Required</p>
            <h1>Socius cannot run without MySQL.</h1>
            <p class="muted"><%= databaseErrorMessage != null ? databaseErrorMessage : "Database connection is required for this environment." %></p>
        </div>
        <div class="stack-list">
            <div class="list-row">
                <strong>Expected database</strong>
                <span>`socius_db` on MySQL</span>
            </div>
            <div class="list-row">
                <strong>Connection file</strong>
                <span>`Socius/src/dao/DBConnection.java`</span>
            </div>
            <div class="list-row">
                <strong>Schema file</strong>
                <span>`Socius/sql/socius_schema.sql`</span>
            </div>
        </div>
    </section>
</main>
<%@ include file="../common/footer.jsp" %>
