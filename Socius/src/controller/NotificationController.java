package controller;

import java.io.IOException;
import java.util.List;

import dao.CommunityDAO;
import dao.NotificationDAO;
import dao.PostDAO;
import dao.ReportDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Notification;
import model.User;

@WebServlet({"/notifications", "/notifications/count"})
public class NotificationController extends BaseController {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        User user = getCurrentUser(request.getSession(false));
        if (user == null) {
            if ("/notifications/count".equals(request.getServletPath())) {
                response.setContentType("application/json");
                response.getWriter().write("{\"count\":0}");
                return;
            }
            redirect(request, response, "/login");
            return;
        }

        NotificationDAO notificationDAO = new NotificationDAO();
        if ("/notifications/count".equals(request.getServletPath())) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().write("{\"count\":" + getNotificationBadgeCount(user, notificationDAO) + "}");
            return;
        }

        List<Notification> notifications = notificationDAO.getNotificationsForUser(user.getUserId(), 40);
        setWorkItemCounts(request, user);
        notificationDAO.markAllRead(user.getUserId());
        request.setAttribute("pageTitle", "Notifications");
        request.setAttribute("notifications", notifications);
        forward(request, response, "/views/member/notifications.jsp");
    }

    private int getNotificationBadgeCount(User user, NotificationDAO notificationDAO) {
        int count = notificationDAO.getUnreadCount(user.getUserId());
        String role = user.getRole();

        if ("moderator".equals(role) || "admin".equals(role)) {
            count += new PostDAO().getPendingPostCount();
            count += new ReportDAO().getOpenReportCount();
        }

        if ("admin".equals(role)) {
            count += new CommunityDAO().getPendingCommunityCount();
        }

        return count;
    }

    private void setWorkItemCounts(HttpServletRequest request, User user) {
        String role = user.getRole();

        if ("moderator".equals(role) || "admin".equals(role)) {
            request.setAttribute("pendingPostWorkCount", Integer.valueOf(new PostDAO().getPendingPostCount()));
            request.setAttribute("openReportWorkCount", Integer.valueOf(new ReportDAO().getOpenReportCount()));
        }

        if ("admin".equals(role)) {
            request.setAttribute("pendingCommunityWorkCount", Integer.valueOf(new CommunityDAO().getPendingCommunityCount()));
        }
    }

    private User getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object currentUser = session.getAttribute("currentUser");
        return currentUser instanceof User ? (User) currentUser : null;
    }
}
