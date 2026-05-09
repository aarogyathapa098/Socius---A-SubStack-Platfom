<%@ page import="java.util.List" %>
<%@ page import="model.Community" %>
<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>
<%
List<Community> allCommunities = (List<Community>) request.getAttribute("allCommunities");
String communityError = (String) request.getAttribute("communityError");
String submittedCommunityName = (String) request.getAttribute("submittedCommunityName");
String submittedCommunityDescription = (String) request.getAttribute("submittedCommunityDescription");
String submittedCommunityGuidelines = (String) request.getAttribute("submittedCommunityGuidelines");
%>
<main class="content-shell">
    <section class="section-card">
        <div class="section-header">
            <div>
                <p class="eyebrow">Existing Communities</p>
                <h2 class="panel-title">Live database communities.</h2>
            </div>
            <button class="button button--primary" type="button" data-toggle-target="createCommunityPanel">
                <span class="material-symbols-outlined">add</span>
                <span>Create community</span>
            </button>
        </div>

        <div
            id="createCommunityPanel"
            class="toggle-panel <%= communityError != null ? "is-open" : "" %>"
            <%= communityError != null ? "" : "hidden" %>
        >
            <div class="section-card section-card--nested">
                <p class="eyebrow">Create Community</p>
                <% if (communityError != null) { %>
                    <div class="flash flash--warning"><%= communityError %></div>
                <% } %>
                <form class="stack-form" action="${pageContext.request.contextPath}/admin/manage-communities" method="post">
                    <label for="communityName">Name</label>
                    <input id="communityName" name="name" type="text" placeholder="Inclusive Design" value="<%= submittedCommunityName != null ? submittedCommunityName : "" %>" required>

                    <label for="communityDesc">Description</label>
                    <textarea id="communityDesc" name="description" rows="4" placeholder="Describe the purpose of the community." required><%= submittedCommunityDescription != null ? submittedCommunityDescription : "" %></textarea>

                    <label for="communityGuidelines">Guidelines</label>
                    <textarea id="communityGuidelines" name="guidelines" rows="5" placeholder="State moderation expectations."><%= submittedCommunityGuidelines != null ? submittedCommunityGuidelines : "" %></textarea>

                    <div class="form-actions">
                        <button class="button button--primary" type="submit">Create community</button>
                    </div>
                </form>
            </div>
        </div>

        <div class="table-scroll">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Slug</th>
                        <th>Members</th>
                        <th>Review</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (allCommunities != null && !allCommunities.isEmpty()) { %>
                        <% for (Community community : allCommunities) { %>
                            <tr>
                                <td><a href="${pageContext.request.contextPath}/community?slug=<%= community.getSlug() %>"><%= community.getName() %></a></td>
                                <td><%= community.getSlug() %></td>
                                <td><%= community.getMemberCount() %></td>
                                <td><span class="badge <%= community.isRequiresReview() ? "badge--mulberry" : "badge--sage" %>"><%= community.isRequiresReview() ? "On" : "Off" %></span></td>
                                <td>
                                    <span class="badge <%= "approved".equals(community.getApprovalStatus()) ? "badge--sage" : ("rejected".equals(community.getApprovalStatus()) ? "badge--rose" : "badge--amber") %>">
                                        <%= community.getApprovalStatus() != null ? community.getApprovalStatus() : "approved" %>
                                    </span>
                                </td>
                                <td>
                                    <% if (!"approved".equals(community.getApprovalStatus())) { %>
                                        <form class="table-action-form" action="${pageContext.request.contextPath}/admin/manage-communities" method="post">
                                            <input type="hidden" name="communityId" value="<%= community.getCommunityId() %>">
                                            <button class="button button--primary button--compact" type="submit" name="action" value="approve">Approve</button>
                                        </form>
                                    <% } %>
                                    <% if (!"rejected".equals(community.getApprovalStatus())) { %>
                                        <form class="table-action-form" action="${pageContext.request.contextPath}/admin/manage-communities" method="post">
                                            <input type="hidden" name="communityId" value="<%= community.getCommunityId() %>">
                                            <button class="button button--ghost button--compact" type="submit" name="action" value="reject">Reject</button>
                                        </form>
                                    <% } %>
                                </td>
                            </tr>
                        <% } %>
                    <% } else { %>
                        <tr>
                            <td colspan="6">No communities found.</td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </section>
</main>
<%@ include file="../common/footer.jsp" %>
