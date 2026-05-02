package controller;

import java.io.IOException;
import java.util.List;

import dao.NotificationDAO;
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
            response.getWriter().write("{\"count\":" + notificationDAO.getUnreadCount(user.getUserId()) + "}");
            return;
        }

        List<Notification> notifications = notificationDAO.getNotificationsForUser(user.getUserId(), 40);
        notificationDAO.markAllRead(user.getUserId());
        request.setAttribute("pageTitle", "Notifications");
        request.setAttribute("notifications", notifications);
        forward(request, response, "/views/member/notifications.jsp");
    }

    private User getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object currentUser = session.getAttribute("currentUser");
        return currentUser instanceof User ? (User) currentUser : null;
    }
}
