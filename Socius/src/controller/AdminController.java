package controller;

import java.io.IOException;
import java.util.List;

import dao.BanDAO;
import dao.CommunityDAO;
import dao.CommunityModeratorDAO;
import dao.CommentDAO;
import dao.NotificationDAO;
import dao.PostDAO;
import dao.ReportDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Community;
import model.CommunityModerator;
import model.Post;
import model.Report;
import model.User;
import util.ValidationUtil;

@WebServlet("/admin/*")
public class AdminController extends BaseController {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        request.setAttribute("pageTitle", resolveTitle(pathInfo));

        if (pathInfo == null || "/dashboard".equals(pathInfo)) {
            loadDashboardData(request);
        } else if ("/manage-communities".equals(pathInfo)) {
            request.setAttribute("allCommunities", new CommunityDAO().getAllCommunitiesForAdmin(200, 0));
        } else if ("/manage-users".equals(pathInfo)) {
            loadUserManagementData(request);
        } else if ("/manage-moderators".equals(pathInfo)) {
            loadModeratorAdminData(request);
        } else if ("/reports".equals(pathInfo)) {
            request.setAttribute("openReports", new ReportDAO().getAllOpenReports());
        }

        forward(request, response, resolveView(pathInfo));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String pathInfo = request.getPathInfo();

        if ("/manage-communities".equals(pathInfo)) {
            String action = trim(request.getParameter("action"));
            if ("approve".equals(action) || "reject".equals(action)) {
                handleCommunityApproval(request, response);
                return;
            }
            handleCreateCommunity(request, response);
            return;
        }

        if ("/reports".equals(pathInfo)) {
            handleReportDecision(request, response);
            return;
        }

        if ("/manage-users".equals(pathInfo)) {
            handleUserManagement(request, response);
            return;
        }

        if ("/manage-moderators".equals(pathInfo)) {
            handleModeratorAssignment(request, response);
            return;
        }

        redirect(request, response, "/admin/dashboard");
    }

    private void loadDashboardData(HttpServletRequest request) {
        UserDAO userDAO = new UserDAO();
        CommunityDAO communityDAO = new CommunityDAO();
        PostDAO postDAO = new PostDAO();
        ReportDAO reportDAO = new ReportDAO();

        List<User> recentUsers = userDAO.getRecentUsers(5);
        List<Post> recentPosts = postDAO.getRecentPosts(5);

        request.setAttribute("adminUserCount", Integer.valueOf(userDAO.getTotalUserCount()));
        request.setAttribute("adminCommunityCount", Integer.valueOf(communityDAO.getTotalCommunityCount()));
        request.setAttribute("adminPendingCommunityCount", Integer.valueOf(communityDAO.getPendingCommunityCount()));
        request.setAttribute("adminPendingCount", Integer.valueOf(postDAO.getPendingPostCount()));
        request.setAttribute("adminReportCount", Integer.valueOf(reportDAO.getOpenReportCount()));
        request.setAttribute("adminRecentUsers", recentUsers);
        request.setAttribute("adminRecentPosts", recentPosts);
    }

    private void loadModeratorAdminData(HttpServletRequest request) {
        request.setAttribute("communities", new CommunityDAO().getAllCommunities(200, 0));
        request.setAttribute("allUsers", new UserDAO().getAllUsers(200, 0));
        request.setAttribute("moderatorAssignments", new CommunityModeratorDAO().getAllModerators());
    }

    private void loadUserManagementData(HttpServletRequest request) {
        UserDAO userDAO = new UserDAO();
        request.setAttribute("allUsers", userDAO.getAllUsers(200, 0));

        Integer editUserId = parseInteger(request.getParameter("editUserId"));
        if (editUserId == null) {
            return;
        }

        User editUser = userDAO.getUserById(editUserId.intValue());
        if (editUser == null) {
            request.setAttribute("userError", "That user could not be found.");
            return;
        }

        request.setAttribute("editUser", editUser);
    }

    private void handleCreateCommunity(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        User admin = getCurrentUser(request.getSession(false));
        if (admin == null) {
            redirect(request, response, "/login");
            return;
        }

        String name = trim(request.getParameter("name"));
        String description = trim(request.getParameter("description"));
        String guidelines = trim(request.getParameter("guidelines"));

        request.setAttribute("pageTitle", "Manage Communities");
        request.setAttribute("submittedCommunityName", name);
        request.setAttribute("submittedCommunityDescription", description);
        request.setAttribute("submittedCommunityGuidelines", guidelines);
        request.setAttribute("allCommunities", new CommunityDAO().getAllCommunitiesForAdmin(200, 0));

        if (isBlank(name) || isBlank(description)) {
            request.setAttribute("communityError", "Community name and description are required.");
            forward(request, response, "/views/admin/manage-communities.jsp");
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
        community.setApprovalStatus("approved");
        community.setCreatedBy(admin.getUserId());

        int communityId = communityDAO.insertCommunity(community);
        if (communityId <= 0) {
            request.setAttribute("communityError", "The community could not be created.");
            forward(request, response, "/views/admin/manage-communities.jsp");
            return;
        }

        request.getSession().setAttribute("flashSuccess", "Community created successfully.");
        redirect(request, response, "/admin/manage-communities");
    }

    private void handleCommunityApproval(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        User admin = getCurrentUser(request.getSession(false));
        if (admin == null) {
            redirect(request, response, "/login");
            return;
        }

        Integer communityId = parseInteger(request.getParameter("communityId"));
        String action = trim(request.getParameter("action"));
        if (communityId != null && ("approve".equals(action) || "reject".equals(action))) {
            CommunityDAO communityDAO = new CommunityDAO();
            Community community = communityDAO.getCommunityById(communityId.intValue());
            String status = "approve".equals(action) ? "approved" : "rejected";
            communityDAO.updateApprovalStatus(communityId.intValue(), status);
            if (community != null) {
                new NotificationDAO().createNotification(
                    community.getCreatedBy(),
                    "approved".equals(status)
                        ? "Your community was approved: " + community.getName()
                        : "Your community was rejected: " + community.getName(),
                    "approved".equals(status) ? "/community?slug=" + community.getSlug() : "/member/my-communities"
                );
            }
            request.getSession().setAttribute(
                "flashSuccess",
                "approved".equals(status) ? "Community approved." : "Community rejected."
            );
        }

        redirect(request, response, "/admin/manage-communities");
    }

    private void handleReportDecision(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        User admin = getCurrentUser(request.getSession(false));
        if (admin == null) {
            redirect(request, response, "/login");
            return;
        }

        Integer reportId = parseInteger(request.getParameter("reportId"));
        String action = trim(request.getParameter("action"));
        if (reportId != null && action != null) {
            ReportDAO reportDAO = new ReportDAO();
            Report report = reportDAO.getReportById(reportId.intValue());
            if (report != null) {
                if ("remove-post".equals(action) && report.getPostId() != null) {
                    new PostDAO().deletePost(report.getPostId().intValue());
                    reportDAO.updateReportStatus(reportId.intValue(), "reviewed", Integer.valueOf(admin.getUserId()));
                } else if ("remove-comment".equals(action) && report.getCommentId() != null) {
                    CommentDAO commentDAO = new CommentDAO();
                    commentDAO.removeComment(report.getCommentId().intValue());
                    if (report.getPostId() != null) {
                        commentDAO.incrementCommentCount(report.getPostId().intValue());
                    }
                    reportDAO.updateReportStatus(reportId.intValue(), "reviewed", Integer.valueOf(admin.getUserId()));
                } else if ("dismiss".equals(action)) {
                    reportDAO.updateReportStatus(reportId.intValue(), "dismissed", Integer.valueOf(admin.getUserId()));
                } else if ("review".equals(action)) {
                    reportDAO.updateReportStatus(reportId.intValue(), "reviewed", Integer.valueOf(admin.getUserId()));
                }

                new NotificationDAO().createNotification(
                    report.getReporterId(),
                    "Your report has been reviewed by the admin team.",
                    "/notifications"
                );
                request.getSession().setAttribute("flashSuccess", "Report updated.");
            }
        }

        redirect(request, response, "/admin/reports");
    }

    private void handleModeratorAssignment(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        User admin = getCurrentUser(request.getSession(false));
        if (admin == null) {
            redirect(request, response, "/login");
            return;
        }

        CommunityModeratorDAO moderatorDAO = new CommunityModeratorDAO();
        UserDAO userDAO = new UserDAO();
        String action = trim(request.getParameter("action"));

        if ("remove".equals(action)) {
            Integer moderatorId = parseInteger(request.getParameter("moderatorId"));
            if (moderatorId != null) {
                Integer userId = moderatorDAO.getUserIdForModeratorAssignment(moderatorId.intValue());
                moderatorDAO.removeModerator(moderatorId.intValue());

                if (userId != null && moderatorDAO.countAssignmentsForUser(userId.intValue()) == 0) {
                    userDAO.updateRole(userId.intValue(), "member");
                }

                request.getSession().setAttribute("flashSuccess", "Moderator assignment removed.");
            }
            redirect(request, response, "/admin/manage-moderators");
            return;
        }

        String username = trim(request.getParameter("username"));
        Integer communityId = parseInteger(request.getParameter("communityId"));

        request.setAttribute("pageTitle", "Manage Moderators");
        request.setAttribute("submittedModeratorUsername", username);
        request.setAttribute("submittedModeratorCommunityId", communityId);
        loadModeratorAdminData(request);

        if (isBlank(username) || communityId == null) {
            request.setAttribute("moderatorError", "Enter a username and choose a community.");
            forward(request, response, "/views/admin/manage-moderators.jsp");
            return;
        }

        User targetUser = userDAO.getUserByUsername(username);
        if (targetUser == null) {
            request.setAttribute("moderatorError", "That username was not found.");
            forward(request, response, "/views/admin/manage-moderators.jsp");
            return;
        }

        if (moderatorDAO.isModeratorAssigned(targetUser.getUserId(), communityId.intValue())) {
            request.setAttribute("moderatorError", "That user is already a moderator for the selected community.");
            forward(request, response, "/views/admin/manage-moderators.jsp");
            return;
        }

        moderatorDAO.assignModerator(targetUser.getUserId(), communityId.intValue(), admin.getUserId());
        userDAO.updateRole(targetUser.getUserId(), "moderator");
        request.getSession().setAttribute("flashSuccess", "Moderator assigned successfully.");
        redirect(request, response, "/admin/manage-moderators");
    }

    private void handleUserManagement(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        User admin = getCurrentUser(request.getSession(false));
        if (admin == null) {
            redirect(request, response, "/login");
            return;
        }

        UserDAO userDAO = new UserDAO();
        Integer userId = parseInteger(request.getParameter("userId"));
        String action = trim(request.getParameter("action"));

        request.setAttribute("pageTitle", "Manage Users");
        request.setAttribute("allUsers", userDAO.getAllUsers(200, 0));

        if (userId == null || action == null) {
            request.setAttribute("userError", "Choose a user action to continue.");
            forward(request, response, "/views/admin/manage-users.jsp");
            return;
        }

        User targetUser = userDAO.getUserById(userId.intValue());
        if (targetUser == null) {
            request.setAttribute("userError", "That user could not be found.");
            forward(request, response, "/views/admin/manage-users.jsp");
            return;
        }
        request.setAttribute("editUser", targetUser);

        if (targetUser.getUserId() == admin.getUserId() && ("ban".equals(action) || "activate".equals(action))) {
            request.setAttribute("userError", "You cannot change your own account access from this screen.");
            forward(request, response, "/views/admin/manage-users.jsp");
            return;
        }

        if ("ban".equals(action)) {
            userDAO.setGlobalBan(targetUser.getUserId(), true);
            new BanDAO().globalBanUser(targetUser.getUserId(), admin.getUserId(), "Global ban applied by admin.");
            request.getSession().setAttribute("flashSuccess", "User banned and marked inactive.");
            redirect(request, response, "/admin/manage-users");
            return;
        }

        if ("activate".equals(action)) {
            userDAO.setGlobalBan(targetUser.getUserId(), false);
            new BanDAO().removeGlobalBansForUser(targetUser.getUserId());
            request.getSession().setAttribute("flashSuccess", "User activated again.");
            redirect(request, response, "/admin/manage-users");
            return;
        }

        if (!"save".equals(action)) {
            request.setAttribute("userError", "Unsupported user action.");
            forward(request, response, "/views/admin/manage-users.jsp");
            return;
        }

        String username = trim(request.getParameter("username"));
        String displayName = trim(request.getParameter("displayName"));
        String email = trim(request.getParameter("email"));
        String phoneNumber = trim(request.getParameter("phoneNumber"));
        String role = trim(request.getParameter("role"));
        Integer penaltyPoints = parseInteger(request.getParameter("penaltyPoints"));
        boolean active = "active".equals(trim(request.getParameter("accountStatus")));
        boolean globallyBanned = "true".equals(trim(request.getParameter("globallyBanned")));

        if (targetUser.getUserId() == admin.getUserId()) {
            role = "admin";
            active = true;
            globallyBanned = false;
        }

        if (!ValidationUtil.isValidUsername(username)) {
            request.setAttribute("userError", "Username must be 3 to 30 characters and can only use letters, numbers, and underscores.");
            forward(request, response, "/views/admin/manage-users.jsp");
            return;
        }

        if (isBlank(displayName) || displayName.length() > 100) {
            request.setAttribute("userError", "Display name is required and must be under 100 characters.");
            forward(request, response, "/views/admin/manage-users.jsp");
            return;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            request.setAttribute("userError", "Enter a valid email address.");
            forward(request, response, "/views/admin/manage-users.jsp");
            return;
        }

        if (!ValidationUtil.isValidPhoneNumber(phoneNumber)) {
            request.setAttribute("userError", "Enter a valid phone number.");
            forward(request, response, "/views/admin/manage-users.jsp");
            return;
        }

        if (!"member".equals(role) && !"moderator".equals(role) && !"admin".equals(role)) {
            request.setAttribute("userError", "Choose a valid role.");
            forward(request, response, "/views/admin/manage-users.jsp");
            return;
        }

        User usernameOwner = userDAO.getUserByUsername(username);
        if (usernameOwner != null && usernameOwner.getUserId() != targetUser.getUserId()) {
            request.setAttribute("userError", "That username is already used by another account.");
            forward(request, response, "/views/admin/manage-users.jsp");
            return;
        }

        User emailOwner = userDAO.getUserByEmail(email);
        if (emailOwner != null && emailOwner.getUserId() != targetUser.getUserId()) {
            request.setAttribute("userError", "That email address is already used by another account.");
            forward(request, response, "/views/admin/manage-users.jsp");
            return;
        }

        User phoneOwner = userDAO.getUserByPhoneNumber(phoneNumber);
        if (phoneOwner != null && phoneOwner.getUserId() != targetUser.getUserId()) {
            request.setAttribute("userError", "That phone number is already used by another account.");
            forward(request, response, "/views/admin/manage-users.jsp");
            return;
        }

        int safePenaltyPoints = penaltyPoints != null ? Math.max(0, penaltyPoints.intValue()) : 0;
        if (globallyBanned) {
            active = false;
        }

        userDAO.updateAdminUser(
            targetUser.getUserId(),
            username,
            displayName,
            email,
            phoneNumber,
            role,
            safePenaltyPoints,
            active,
            globallyBanned
        );

        BanDAO banDAO = new BanDAO();
        if (globallyBanned && !banDAO.isGloballyBanned(targetUser.getUserId())) {
            banDAO.globalBanUser(targetUser.getUserId(), admin.getUserId(), "Global ban applied by admin.");
        } else if (!globallyBanned && targetUser.isGloballyBanned()) {
            banDAO.removeGlobalBansForUser(targetUser.getUserId());
        }

        if (targetUser.getUserId() == admin.getUserId()) {
            request.getSession().setAttribute("currentUser", userDAO.getUserById(admin.getUserId()));
        }

        request.getSession().setAttribute("flashSuccess", "User updated.");
        redirect(request, response, "/admin/manage-users");
    }

    private String resolveView(String pathInfo) {
        if (pathInfo == null || "/dashboard".equals(pathInfo)) {
            return "/views/admin/dashboard.jsp";
        }
        if ("/manage-communities".equals(pathInfo)) {
            return "/views/admin/manage-communities.jsp";
        }
        if ("/manage-users".equals(pathInfo)) {
            return "/views/admin/manage-users.jsp";
        }
        if ("/manage-moderators".equals(pathInfo)) {
            return "/views/admin/manage-moderators.jsp";
        }
        if ("/reports".equals(pathInfo)) {
            return "/views/admin/reports.jsp";
        }
        return "/views/admin/dashboard.jsp";
    }

    private String resolveTitle(String pathInfo) {
        if (pathInfo == null || "/dashboard".equals(pathInfo)) {
            return "Admin";
        }
        if ("/manage-communities".equals(pathInfo)) {
            return "Manage Communities";
        }
        if ("/manage-users".equals(pathInfo)) {
            return "Manage Users";
        }
        if ("/manage-moderators".equals(pathInfo)) {
            return "Manage Moderators";
        }
        if ("/reports".equals(pathInfo)) {
            return "Reports";
        }
        return "Admin";
    }

    private User getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object currentUser = session.getAttribute("currentUser");
        return currentUser instanceof User ? (User) currentUser : null;
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

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
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
}
