package controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import dao.BanDAO;
import dao.CommunityDAO;
import dao.CommunityMembershipDAO;
import dao.PostDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Part;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Community;
import model.Post;
import model.User;
import util.UploadPathUtil;

@MultipartConfig(
    fileSizeThreshold = 1048576,
    maxFileSize = 5242880,
    maxRequestSize = 7340032
)
@WebServlet("/member/*")
public class MemberController extends BaseController {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        request.setAttribute("pageTitle", resolveTitle(pathInfo));

        if (pathInfo == null || "/home".equals(pathInfo)) {
            loadHomeData(request);
        } else if ("/dashboard".equals(pathInfo)) {
            loadDashboardData(request);
        } else if ("/my-posts".equals(pathInfo)) {
            loadPostListData(request);
        } else if ("/my-communities".equals(pathInfo)) {
            loadCommunityData(request);
        } else if ("/create-post".equals(pathInfo)) {
            loadCreatePostData(request);
        } else if ("/profile".equals(pathInfo)) {
            loadDashboardData(request);
        }

        forward(request, response, resolveView(pathInfo));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String pathInfo = request.getPathInfo();

        if ("/create-post".equals(pathInfo)) {
            try {
                handleCreatePost(request, response);
            } catch (IllegalStateException exception) {
                request.setAttribute("pageTitle", "Create Post");
                request.setAttribute("postError", "The image is too large. Upload one image up to 5 MB.");
                loadCreatePostData(request);
                forward(request, response, "/views/member/create-post.jsp");
            }
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

        if ("/profile".equals(pathInfo)) {
            handleProfileUpdate(request, response);
            return;
        }

        redirect(request, response, "/member/home");
    }

    private void loadHomeData(HttpServletRequest request) {
        PostDAO postDAO = new PostDAO();
        CommunityDAO communityDAO = new CommunityDAO();
        User user = getCurrentUser(request.getSession(false));
        String feedMode = trim(request.getParameter("mode"));
        if (!"explore".equals(feedMode)) {
            feedMode = "personalized";
        }

        request.setAttribute(
            "feedPosts",
            "personalized".equals(feedMode) && user != null
                ? postDAO.getPersonalizedFeed(user.getUserId())
                : postDAO.getExploreFeed()
        );
        request.setAttribute("feedMode", feedMode);
        request.setAttribute("suggestedCommunities", communityDAO.getTrendingCommunities(6));
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
        String imageAltText = trim(request.getParameter("imageAltText"));
        String submissionAction = trim(request.getParameter("submissionAction"));
        String status = "save-draft".equals(submissionAction) ? "draft" : "pending";
        Part imagePart = request.getPart("postImage");
        boolean hasImageUpload = imagePart != null && imagePart.getSize() > 0;

        request.setAttribute("pageTitle", "Create Post");
        request.setAttribute("submittedCommunityId", communityId);
        request.setAttribute("submittedTitle", title);
        request.setAttribute("submittedPostType", postType);
        request.setAttribute("submittedContent", content);
        request.setAttribute("submittedImageAltText", imageAltText);
        loadCreatePostData(request);

        if (communityId == null || isBlank(title) || title.length() < 5 || (isBlank(content) && !hasImageUpload)) {
            request.setAttribute(
                "postError",
                "Choose a community, add a clear title, and include either post text or an image."
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

        String imageUrl;
        try {
            imageUrl = savePostImage(request);
        } catch (IllegalArgumentException exception) {
            request.setAttribute("postError", exception.getMessage());
            forward(request, response, "/views/member/create-post.jsp");
            return;
        }

        Post post = new Post();
        post.setCommunityId(communityId.intValue());
        post.setAuthorId(user.getUserId());
        post.setTitle(title);
        post.setContent(content != null ? content : "");
        post.setPostType(resolvePostType(postType, imageUrl));
        post.setImageUrl(imageUrl);
        post.setImageAltText(!isBlank(imageAltText) ? imageAltText : title);
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
        if (communityDAO.getCommunityByName(name) != null) {
            request.setAttribute("communityError", "Community name already exists.");
            forward(request, response, "/views/member/create-community.jsp");
            return;
        }

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
        community.setApprovalStatus("pending");
        community.setCreatedBy(user.getUserId());

        int communityId;
        try {
            communityId = communityDAO.insertCommunity(community);
        } catch (RuntimeException exception) {
            request.setAttribute("communityError", "Community name already exists.");
            forward(request, response, "/views/member/create-community.jsp");
            return;
        }

        if (communityId <= 0) {
            request.setAttribute("communityError", "The community could not be created. Please try again.");
            forward(request, response, "/views/member/create-community.jsp");
            return;
        }

        new CommunityMembershipDAO().joinCommunity(user.getUserId(), communityId);
        request.getSession().setAttribute(
            "flashSuccess",
            "Community submitted for admin approval. It will appear publicly after approval."
        );
        redirect(request, response, "/member/my-communities");
    }

    private void handleProfileUpdate(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        User user = getCurrentUser(request.getSession(false));
        if (user == null) {
            redirect(request, response, "/login");
            return;
        }

        String displayName = trim(request.getParameter("displayName"));
        String phoneNumber = trim(request.getParameter("phoneNumber"));
        String bio = trim(request.getParameter("bio"));

        request.setAttribute("pageTitle", "Profile");

        if (isBlank(displayName) || displayName.length() > 100 || (bio != null && bio.length() > 1000)) {
            request.setAttribute("profileError", "Add a display name under 100 characters and keep the bio concise.");
            loadDashboardData(request);
            forward(request, response, "/views/member/profile.jsp");
            return;
        }

        UserDAO userDAO = new UserDAO();
        User phoneOwner = !isBlank(phoneNumber) ? userDAO.getUserByPhoneNumber(phoneNumber) : null;
        if (phoneOwner != null && phoneOwner.getUserId() != user.getUserId()) {
            request.setAttribute("profileError", "That phone number is already linked to another account.");
            loadDashboardData(request);
            forward(request, response, "/views/member/profile.jsp");
            return;
        }

        userDAO.updateProfile(user.getUserId(), displayName, bio, phoneNumber);
        User refreshedUser = userDAO.getUserById(user.getUserId());
        request.getSession().setAttribute("currentUser", refreshedUser);
        request.getSession().setAttribute("flashSuccess", "Profile updated.");
        redirect(request, response, "/member/profile");
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
        if (pathInfo == null || "/home".equals(pathInfo)) {
            return "/views/member/home.jsp";
        }
        if ("/dashboard".equals(pathInfo)) {
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
        if (pathInfo == null || "/home".equals(pathInfo)) {
            return "Home";
        }
        if ("/dashboard".equals(pathInfo)) {
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

    private String savePostImage(HttpServletRequest request) throws IOException, ServletException {
        Part imagePart = request.getPart("postImage");
        if (imagePart == null || imagePart.getSize() == 0) {
            return null;
        }

        String extension = resolveImageExtension(imagePart.getContentType());
        if (extension == null) {
            throw new IllegalArgumentException("Upload a JPG, PNG, GIF, or WebP image only.");
        }

        Path uploadDirectory = UploadPathUtil.getPostImageDirectory();
        Files.createDirectories(uploadDirectory);

        String fileName = "post-" + System.currentTimeMillis() + "-" + UUID.randomUUID() + "." + extension;
        Path destination = uploadDirectory.resolve(fileName).toAbsolutePath().normalize();
        try (InputStream inputStream = imagePart.getInputStream()) {
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
        }
        imagePart.delete();

        return UploadPathUtil.POST_IMAGE_URL_PREFIX + fileName;
    }

    private String resolveImageExtension(String contentType) {
        if (contentType == null) {
            return null;
        }

        String normalizedType = contentType.toLowerCase(Locale.ROOT);
        if ("image/jpeg".equals(normalizedType) || "image/jpg".equals(normalizedType)) {
            return "jpg";
        }
        if ("image/png".equals(normalizedType)) {
            return "png";
        }
        if ("image/gif".equals(normalizedType)) {
            return "gif";
        }
        if ("image/webp".equals(normalizedType)) {
            return "webp";
        }
        return null;
    }

    private String resolvePostType(String postType, String imageUrl) {
        if (imageUrl != null) {
            return "image";
        }

        if (postType == null) {
            return "text";
        }

        String normalizedPostType = postType.toLowerCase(Locale.ROOT);
        if (
            "text".equals(normalizedPostType)
                || "resource".equals(normalizedPostType)
                || "event".equals(normalizedPostType)
        ) {
            return normalizedPostType;
        }

        return "text";
    }
}
