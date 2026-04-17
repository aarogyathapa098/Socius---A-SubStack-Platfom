package controller;

import java.io.IOException;
import java.util.List;

import dao.CommunityDAO;
import dao.CommunityModeratorDAO;
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
            request.setAttribute("allCommunities", new CommunityDAO().getAllCommunities(200, 0));
        } else if ("/manage-users".equals(pathInfo)) {
            request.setAttribute("allUsers", new UserDAO().getAllUsers(200, 0));
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
            handleCreateCommunity(request, response);
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
        request.setAttribute("allCommunities", new CommunityDAO().getAllCommunities(200, 0));

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
