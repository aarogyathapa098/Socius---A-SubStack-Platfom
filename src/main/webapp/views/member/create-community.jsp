<%@ include file="../common/header.jsp" %>
<%@ include file="../common/sidebar.jsp" %>
<%
String communityError = (String) request.getAttribute("communityError");
String submittedCommunityName = (String) request.getAttribute("submittedCommunityName");
String submittedCommunityDescription = (String) request.getAttribute("submittedCommunityDescription");
String submittedCommunityGuidelines = (String) request.getAttribute("submittedCommunityGuidelines");
%>
<main class="content-shell">
    <section class="section-card">
        <p class="eyebrow">Create Community</p>
        <h1>Start a new discussion space.</h1>
        <p class="lead-sm">This works more like a subreddit or newsletter section: the creator opens the space and members can join it afterwards.</p>
    </section>

    <% if (communityError != null) { %>
        <div class="flash flash--warning"><%= communityError %></div>
    <% } %>

    <section class="section-card">
        <form class="stack-form" action="${pageContext.request.contextPath}/member/create-community" method="post">
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
    </section>
</main>
<%@ include file="../common/footer.jsp" %>
