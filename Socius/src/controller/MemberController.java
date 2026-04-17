package controller;

import java.io.IOException;
import java.util.List;

import dao.BanDAO;
import dao.CommunityDAO;
import dao.CommunityMembershipDAO;
import dao.PostDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Community;
import model.Post;
import model.User;

@WebServlet("/member/*")
public class MemberController extends BaseController {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        request.setAttribute("pageTitle", resolveTitle(pathInfo));

        if (pathInfo == null || "/dashboard".equals(pathInfo)) {
            loadDashboardData(request);
        } else if ("/my-posts".equals(pathInfo)) {
            loadPostListData(request);
        } else if ("/my-communities".equals(pathInfo)) {
            loadCommunityData(request);
        } else if ("/create-post".equals(pathInfo)) {
            loadCreatePostData(request);
        }

        forward(request, response, resolveView(pathInfo));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String pathInfo = request.getPathInfo();

        if ("/create-post".equals(pathInfo)) {
            handleCreatePost(request, response);
            return;
        }

        if ("/create-community".equals(pathInfo)) {
            handleCreateCommunity(request, response);
            return;
        }

        if ("/join-community".equals(pathInfo)) {
            handleJoinCommunity(request, response);
            return;
        }

        redirect(request, response, "/member/dashboard");
    }

    private void loadDashboardData(HttpServletRequest request) {
        User user = getCurrentUser(request.getSession(false));
        if (user == null) {
            return;
        }

        PostDAO postDAO = new PostDAO();
        CommunityMembershipDAO membershipDAO = new CommunityMembershipDAO();
        List<Post> posts = postDAO.getPostsByAuthor(user.getUserId());
        List<Community> communities = membershipDAO.getCommunitiesForUser(user.getUserId());

        int pendingCount = 0;
        for (Post post : posts) {
            if ("pending".equals(post.getStatus())) {
                pendingCount++;
            }
        }

        request.setAttribute("memberPosts", posts);
        request.setAttribute("memberCommunities", communities);
        request.setAttribute("memberPostCount", Integer.valueOf(posts.size()));
        request.setAttribute("memberPendingCount", Integer.valueOf(pendingCount));
        request.setAttribute("memberCommunityCount", Integer.valueOf(communities.size()));
    }

    private void loadPostListData(HttpServletRequest request) {
        User user = getCurrentUser(request.getSession(false));
        if (user == null) {
            return;
        }

        request.setAttribute("memberPosts", new PostDAO().getPostsByAuthor(user.getUserId()));
    }

    private void loadCommunityData(HttpServletRequest request) {
        User user = getCurrentUser(request.getSession(false));
        if (user == null) {
            return;
        }

        request.setAttribute(
            "memberCommunities",
            new CommunityMembershipDAO().getCommunitiesForUser(user.getUserId())
        );
    }

    private void loadCreatePostData(HttpServletRequest request) {
        request.setAttribute("availableCommunities", new CommunityDAO().getAllCommunities(100, 0));
    }

    private void handleCreatePost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        User user = getCurrentUser(request.getSession(false));
        if (user == null) {
            redirect(request, response, "/login");
            return;
        }

        Integer communityId = parseInteger(request.getParameter("communityId"));
        String title = trim(request.getParameter("title"));
        String postType = trim(request.getParameter("postType"));
        String content = trim(request.getParameter("content"));
        String submissionAction = trim(request.getParameter("submissionAction"));
        String status = "save-draft".equals(submissionAction) ? "draft" : "pending";

        request.setAttribute("pageTitle", "Create Post");
        request.setAttribute("submittedCommunityId", communityId);
        request.setAttribute("submittedTitle", title);
        request.setAttribute("submittedPostType", postType);
        request.setAttribute("submittedContent", content);
        loadCreatePostData(request);

        if (communityId == null || isBlank(title) || title.length() < 5 || isBlank(content)) {
            request.setAttribute(
                "postError",
                "Choose a community and provide a clear title and post content before saving."
            );
            forward(request, response, "/views/member/create-post.jsp");
            return;
        }

        BanDAO banDAO = new BanDAO();
        if (banDAO.isBannedFromCommunity(user.getUserId(), communityId.intValue())) {
            request.setAttribute(
                "postError",
                "You are currently banned from posting in that community."
            );
            forward(request, response, "/views/member/create-post.jsp");
            return;
        }

        Post post = new Post();
        post.setCommunityId(communityId.intValue());
        post.setAuthorId(user.getUserId());
        post.setTitle(title);
        post.setContent(content);
        post.setPostType(postType != null ? postType.toLowerCase() : "text");
        post.setStatus(status);

        int postId = new PostDAO().insertPost(post);

        if (postId <= 0) {
            request.setAttribute("postError", "The post could not be saved. Please try again.");
            forward(request, response, "/views/member/create-post.jsp");
            return;
        }

        request.getSession().setAttribute(
            "flashSuccess",
            "draft".equals(status)
                ? "Post saved as a draft."
                : "Post submitted for moderator review."
        );
        redirect(request, response, "/member/my-posts");
    }

    private void handleCreateCommunity(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        User user = getCurrentUser(request.getSession(false));
        if (user == null) {
            redirect(request, response, "/login");
            return;
        }

        String name = trim(request.getParameter("name"));
        String description = trim(request.getParameter("description"));
        String guidelines = trim(request.getParameter("guidelines"));

        request.setAttribute("pageTitle", "Create Community");
        request.setAttribute("submittedCommunityName", name);
        request.setAttribute("submittedCommunityDescription", description);
        request.setAttribute("submittedCommunityGuidelines", guidelines);

        if (isBlank(name) || isBlank(description)) {
            request.setAttribute("communityError", "Community name and description are required.");
            forward(request, response, "/views/member/create-community.jsp");
            return;
        }

        CommunityDAO communityDAO = new CommunityDAO();
        String slug = buildUniqueSlug(name, communityDAO);

        Community community = new Community();
        community.setName(name);
        community.setSlug(slug);
        community.setDescription(description);
        community.setGuidelines(guidelines);
        community.setBannerStyle("minimal");
        community.setIconName("forum");
        community.setPrivateCommunity(false);
        community.setRequiresReview(true);
        community.setCreatedBy(user.getUserId());

        int communityId = communityDAO.insertCommunity(community);
        if (communityId <= 0) {
            request.setAttribute("communityError", "The community could not be created. Please try again.");
            forward(request, response, "/views/member/create-community.jsp");
            return;
        }

        new CommunityMembershipDAO().joinCommunity(user.getUserId(), communityId);
        request.getSession().setAttribute("flashSuccess", "Community created successfully.");
        redirect(request, response, "/community?slug=" + slug);
    }

    private void handleJoinCommunity(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        User user = getCurrentUser(request.getSession(false));
        if (user == null) {
            redirect(request, response, "/login");
            return;
        }

        Integer communityId = parseInteger(request.getParameter("communityId"));
        String slug = trim(request.getParameter("slug"));

        if (communityId != null) {
            CommunityMembershipDAO membershipDAO = new CommunityMembershipDAO();
            if (!membershipDAO.isMember(user.getUserId(), communityId.intValue())) {
                membershipDAO.joinCommunity(user.getUserId(), communityId.intValue());
                request.getSession().setAttribute("flashSuccess", "You joined the community.");
            }
        }

        redirect(request, response, slug != null ? "/community?slug=" + slug : "/community");
    }

    private User getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object currentUser = session.getAttribute("currentUser");
        return currentUser instanceof User ? (User) currentUser : null;
    }

    private String resolveView(String pathInfo) {
        if (pathInfo == null || "/dashboard".equals(pathInfo)) {
            return "/views/member/dashboard.jsp";
        }
        if ("/my-posts".equals(pathInfo)) {
            return "/views/member/my-posts.jsp";
        }
        if ("/create-post".equals(pathInfo)) {
            return "/views/member/create-post.jsp";
        }
        if ("/create-community".equals(pathInfo)) {
            return "/views/member/create-community.jsp";
        }
        if ("/edit-post".equals(pathInfo)) {
            return "/views/member/edit-post.jsp";
        }
        if ("/my-communities".equals(pathInfo)) {
            return "/views/member/my-communities.jsp";
        }
        if ("/profile".equals(pathInfo)) {
            return "/views/member/profile.jsp";
        }
        return "/views/member/dashboard.jsp";
    }

    private String resolveTitle(String pathInfo) {
        if (pathInfo == null || "/dashboard".equals(pathInfo)) {
            return "Member Portal";
        }
        if ("/my-posts".equals(pathInfo)) {
            return "My Posts";
        }
        if ("/create-post".equals(pathInfo)) {
            return "Create Post";
        }
        if ("/create-community".equals(pathInfo)) {
            return "Create Community";
        }
        if ("/my-communities".equals(pathInfo)) {
            return "My Communities";
        }
        if ("/profile".equals(pathInfo)) {
            return "Profile";
        }
        return "Member Portal";
    }

    private Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return Integer.valueOf(Integer.parseInt(value.trim()));
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private String buildUniqueSlug(String name, CommunityDAO communityDAO) {
        String baseSlug =
            name.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        String slug = baseSlug.isEmpty() ? "community" : baseSlug;
        int suffix = 2;

        while (communityDAO.getCommunityBySlug(slug) != null) {
            slug = baseSlug + "-" + suffix;
            suffix++;
        }

        return slug;
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
