package controller;

import java.io.IOException;
import java.util.List;

import dao.BanDAO;
import dao.BulletinDAO;
import dao.CommentDAO;
import dao.CommunityDAO;
import dao.NotificationDAO;
import dao.PostDAO;
import dao.ReportDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Ban;
import model.Bulletin;
import model.Community;
import model.Post;
import model.Report;
import model.User;

@WebServlet("/moderator/*")
public class ModeratorController extends BaseController {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        request.setAttribute("pageTitle", resolveTitle(pathInfo));

        if (pathInfo == null || "/dashboard".equals(pathInfo)) {
            loadDashboardData(request);
        } else if ("/approval-queue".equals(pathInfo)) {
            request.setAttribute("pendingPosts", new PostDAO().getAllPendingPosts());
        } else if ("/reported-posts".equals(pathInfo)) {
            request.setAttribute("openReports", new ReportDAO().getAllOpenReports());
        } else if ("/banned-members".equals(pathInfo)) {
            loadBanData(request);
        } else if ("/send-bulletin".equals(pathInfo)) {
            loadBulletinData(request);
        }

        forward(request, response, resolveView(pathInfo));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String pathInfo = request.getPathInfo();

        if ("/approval-queue".equals(pathInfo)) {
            handleApprovalDecision(request, response);
            return;
        }

        if ("/reported-posts".equals(pathInfo)) {
            handleReportDecision(request, response);
            return;
        }

        if ("/send-bulletin".equals(pathInfo)) {
            handleBulletin(request, response);
            return;
        }

        if ("/banned-members".equals(pathInfo)) {
            handleBanAction(request, response);
            return;
        }

        redirect(request, response, "/moderator/dashboard");
    }

    private void loadDashboardData(HttpServletRequest request) {
        PostDAO postDAO = new PostDAO();
        ReportDAO reportDAO = new ReportDAO();
        BanDAO banDAO = new BanDAO();
        BulletinDAO bulletinDAO = new BulletinDAO();

        request.setAttribute("moderatorPendingCount", Integer.valueOf(postDAO.getPendingPostCount()));
        request.setAttribute("moderatorReportCount", Integer.valueOf(reportDAO.getOpenReportCount()));
        request.setAttribute("moderatorBanCount", Integer.valueOf(banDAO.getAllBans().size()));
        request.setAttribute("moderatorBulletinCount", Integer.valueOf(bulletinDAO.getTotalBulletinCount()));
    }

    private void loadBanData(HttpServletRequest request) {
        request.setAttribute("allBans", new BanDAO().getAllBans());
        request.setAttribute("communities", new CommunityDAO().getAllCommunities(100, 0));
    }

    private void loadBulletinData(HttpServletRequest request) {
        BulletinDAO bulletinDAO = new BulletinDAO();
        request.setAttribute("communities", new CommunityDAO().getAllCommunities(100, 0));
        request.setAttribute("recentBulletins", bulletinDAO.getRecentBulletins(6));
    }

    private void handleApprovalDecision(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        User moderator = getCurrentUser(request.getSession(false));
        if (moderator == null) {
            redirect(request, response, "/login");
            return;
        }

        Integer postId = parseInteger(request.getParameter("postId"));
        String decision = trim(request.getParameter("decision"));
        String rejectionReason = trim(request.getParameter("rejectionReason"));

        if (postId != null && ("approve".equals(decision) || "reject".equals(decision))) {
            PostDAO postDAO = new PostDAO();
            Post post = postDAO.getPostForModeration(postId.intValue());
            postDAO.updateStatus(
                postId.intValue(),
                "approve".equals(decision) ? "approved" : "rejected",
                Integer.valueOf(moderator.getUserId()),
                "reject".equals(decision) ? rejectionReason : null
            );
            if (post != null) {
                new NotificationDAO().createNotification(
                    post.getAuthorId(),
                    "approve".equals(decision)
                        ? "Your post was approved: " + post.getTitle()
                        : "Your post was rejected: " + post.getTitle(),
                    "approve".equals(decision) ? "/post?id=" + post.getPostId() : "/member/my-posts"
                );
            }
            request.getSession().setAttribute(
                "flashSuccess",
                "approve".equals(decision)
                    ? "Post approved and published."
                    : "Post rejected with moderator feedback."
            );
        }

        redirect(request, response, "/moderator/approval-queue");
    }

    private void handleReportDecision(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        User moderator = getCurrentUser(request.getSession(false));
        if (moderator == null) {
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
                    reportDAO.updateReportStatus(reportId.intValue(), "reviewed", Integer.valueOf(moderator.getUserId()));
                } else if ("remove-comment".equals(action) && report.getCommentId() != null) {
                    CommentDAO commentDAO = new CommentDAO();
                    commentDAO.removeComment(report.getCommentId().intValue());
                    if (report.getPostId() != null) {
                        commentDAO.incrementCommentCount(report.getPostId().intValue());
                    }
                    reportDAO.updateReportStatus(reportId.intValue(), "reviewed", Integer.valueOf(moderator.getUserId()));
                } else if ("dismiss".equals(action)) {
                    reportDAO.updateReportStatus(reportId.intValue(), "dismissed", Integer.valueOf(moderator.getUserId()));
                } else if ("review".equals(action)) {
                    reportDAO.updateReportStatus(reportId.intValue(), "reviewed", Integer.valueOf(moderator.getUserId()));
                }

                new NotificationDAO().createNotification(
                    report.getReporterId(),
                    "Your report has been reviewed by moderators.",
                    "/notifications"
                );
                request.getSession().setAttribute("flashSuccess", "Report updated.");
            }
        }

        redirect(request, response, "/moderator/reported-posts");
    }

    private void handleBulletin(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        User moderator = getCurrentUser(request.getSession(false));
        if (moderator == null) {
            redirect(request, response, "/login");
            return;
        }

        Integer communityId = parseInteger(request.getParameter("communityId"));
        String subject = trim(request.getParameter("subject"));
        String body = trim(request.getParameter("body"));

        request.setAttribute("pageTitle", "Send Bulletin");
        request.setAttribute("submittedBulletinCommunityId", communityId);
        request.setAttribute("submittedBulletinSubject", subject);
        request.setAttribute("submittedBulletinBody", body);
        loadBulletinData(request);

        if (communityId == null || isBlank(subject) || isBlank(body)) {
            request.setAttribute("bulletinError", "Choose a community, add a subject, and write the bulletin body.");
            forward(request, response, "/views/mod/send-bulletin.jsp");
            return;
        }

        Community community = new CommunityDAO().getCommunityById(communityId.intValue());
        Bulletin bulletin = new Bulletin();
        bulletin.setCommunityId(communityId.intValue());
        bulletin.setSentBy(moderator.getUserId());
        bulletin.setSubject(subject);
        bulletin.setBody(body);
        bulletin.setRecipientCount(community != null ? community.getMemberCount() : 0);
        new BulletinDAO().insertBulletin(bulletin);

        request.getSession().setAttribute("flashSuccess", "Bulletin sent to the selected community.");
        redirect(request, response, "/moderator/send-bulletin");
    }

    private void handleBanAction(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        User moderator = getCurrentUser(request.getSession(false));
        if (moderator == null) {
            redirect(request, response, "/login");
            return;
        }

        BanDAO banDAO = new BanDAO();
        String action = trim(request.getParameter("action"));

        if ("remove".equals(action)) {
            Integer banId = parseInteger(request.getParameter("banId"));
            if (banId != null) {
                banDAO.unbanUser(banId.intValue());
                request.getSession().setAttribute("flashSuccess", "Ban removed.");
            }
            redirect(request, response, "/moderator/banned-members");
            return;
        }

        String username = trim(request.getParameter("username"));
        Integer communityId = parseInteger(request.getParameter("communityId"));
        String reason = trim(request.getParameter("reason"));

        request.setAttribute("pageTitle", "Banned Members");
        request.setAttribute("submittedBanUsername", username);
        request.setAttribute("submittedBanCommunityId", communityId);
        request.setAttribute("submittedBanReason", reason);
        loadBanData(request);

        if (isBlank(username) || communityId == null || isBlank(reason)) {
            request.setAttribute("banError", "Enter a username, choose a community, and give a reason.");
            forward(request, response, "/views/mod/banned-members.jsp");
            return;
        }

        User targetUser = new UserDAO().getUserByUsername(username);
        if (targetUser == null) {
            request.setAttribute("banError", "That username was not found.");
            forward(request, response, "/views/mod/banned-members.jsp");
            return;
        }

        if (banDAO.isBannedFromCommunity(targetUser.getUserId(), communityId.intValue())) {
            request.setAttribute("banError", "That user is already banned from the selected community.");
            forward(request, response, "/views/mod/banned-members.jsp");
            return;
        }

        banDAO.banUserFromCommunity(
            targetUser.getUserId(),
            communityId.intValue(),
            moderator.getUserId(),
            reason,
            null
        );
        request.getSession().setAttribute("flashSuccess", "Community ban applied.");
        redirect(request, response, "/moderator/banned-members");
    }

    private String resolveView(String pathInfo) {
        if (pathInfo == null || "/dashboard".equals(pathInfo)) {
            return "/views/mod/dashboard.jsp";
        }
        if ("/approval-queue".equals(pathInfo)) {
            return "/views/mod/approval-queue.jsp";
        }
        if ("/reported-posts".equals(pathInfo)) {
            return "/views/mod/reported-posts.jsp";
        }
        if ("/banned-members".equals(pathInfo)) {
            return "/views/mod/banned-members.jsp";
        }
        if ("/send-bulletin".equals(pathInfo)) {
            return "/views/mod/send-bulletin.jsp";
        }
        return "/views/mod/dashboard.jsp";
    }

    private String resolveTitle(String pathInfo) {
        if (pathInfo == null || "/dashboard".equals(pathInfo)) {
            return "Moderator";
        }
        if ("/approval-queue".equals(pathInfo)) {
            return "Approval Queue";
        }
        if ("/reported-posts".equals(pathInfo)) {
            return "Reported Posts";
        }
        if ("/banned-members".equals(pathInfo)) {
            return "Banned Members";
        }
        if ("/send-bulletin".equals(pathInfo)) {
            return "Send Bulletin";
        }
        return "Moderator";
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
}
