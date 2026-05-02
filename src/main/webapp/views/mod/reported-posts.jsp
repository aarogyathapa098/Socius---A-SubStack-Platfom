<%@ page import="java.util.List" %>
<%@ page import="model.Report" %>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>
<%
List<Report> openReports = (List<Report>) request.getAttribute("openReports");
%>
<main class="content-shell">
    <section class="section-header">
        <div>
            <p class="eyebrow">Reports</p>
            <h1>Reported content and moderation follow-up.</h1>
        </div>
    </section>

    <section class="section-card">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Reason</th>
                    <th>Content</th>
                    <th>Reporter</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <% if (openReports != null && !openReports.isEmpty()) { %>
                    <% for (Report report : openReports) { %>
                        <tr>
                            <td><%= report.getReason() %></td>
                            <td>
                                <%= report.getCommentId() != null && report.getCommentContent() != null
                                    ? report.getCommentContent()
                                    : (report.getPostTitle() != null ? report.getPostTitle() : "Unknown content") %>
                            </td>
                            <td><%= report.getReporterUsername() %></td>
                            <td><span class="badge badge--amber"><%= report.getStatus() %></span></td>
                            <td>
                                <form class="table-action-form" action="${pageContext.request.contextPath}/moderator/reported-posts" method="post">
                                    <input type="hidden" name="reportId" value="<%= report.getReportId() %>">
                                    <button class="button button--primary button--compact" type="submit" name="action" value="review">Mark reviewed</button>
                                    <button class="button button--ghost button--compact" type="submit" name="action" value="dismiss">Dismiss</button>
                                    <% if (report.getPostId() != null && report.getCommentId() == null) { %>
                                        <button class="button button--danger button--compact" type="submit" name="action" value="remove-post">Remove post</button>
                                    <% } %>
                                    <% if (report.getCommentId() != null) { %>
                                        <button class="button button--danger button--compact" type="submit" name="action" value="remove-comment">Remove comment</button>
                                    <% } %>
                                </form>
                            </td>
                        </tr>
                    <% } %>
                <% } else { %>
                    <tr>
                        <td colspan="5">No open reports found.</td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </section>
</main>
<%@ include file="../common/footer.jsp" %>
