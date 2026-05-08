package controller;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import dao.DBConnection;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.User;
import service.AuthService;
import util.PasswordUtil;

@WebServlet("/login")
public class LoginController extends BaseController {

    private static final long LOCK_SECONDS = 60L;

    private final AuthService authService = new AuthService();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") instanceof User) {
            redirect(request, response, "/member/home");
            return;
        }

        request.setAttribute("showSidebar", Boolean.FALSE);
        request.setAttribute("pageTitle", "Sign In");
        request.setAttribute("databaseConnected", Boolean.valueOf(DBConnection.isAvailable()));
        forward(request, response, "/views/auth/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String username = trim(request.getParameter("username"));
        String password = request.getParameter("password");

        if (!authService.isLoginInputValid(username, password)) {
            showLoginError(request, response, username, "Invalid username/email or password.");
            return;
        }

        User user = username.contains("@")
            ? userDAO.getUserByEmail(username)
            : userDAO.getUserByUsername(username);

        if (user == null || !user.isActive() || user.isGloballyBanned()) {
            showLoginError(request, response, username, "Invalid username/email or password.");
            return;
        }

        if (user.getLockedUntil() != null) {
            long remainingSeconds = getRemainingLockSeconds(user);
            if (remainingSeconds <= 0L || remainingSeconds > LOCK_SECONDS) {
                userDAO.resetFailedAttempts(user.getUserId());
                user = userDAO.getUserById(user.getUserId());
            } else {
                showLockedError(request, response, username, remainingSeconds);
                return;
            }
        }

        if (!PasswordUtil.verify(password, user.getPasswordHash())) {
            int failedAttempts = user.getFailedAttempts() + 1;
            userDAO.incrementFailedAttempts(user.getUserId());

            if (failedAttempts >= 5) {
                userDAO.lockAccount(user.getUserId());
                showLockedError(request, response, username, LOCK_SECONDS);
                return;
            }

            showLoginError(request, response, username, "Invalid username/email or password.");
            return;
        }

        userDAO.resetFailedAttempts(user.getUserId());
        user = userDAO.getUserById(user.getUserId());

        HttpSession session = request.getSession();
        session.setAttribute("currentUser", user);

        redirect(request, response, "/member/home");
    }

    private void showLoginError(
        HttpServletRequest request,
        HttpServletResponse response,
        String username,
        String message
    ) throws ServletException, IOException {
        request.setAttribute("showSidebar", Boolean.FALSE);
        request.setAttribute("pageTitle", "Sign In");
        request.setAttribute("loginError", message);
        request.setAttribute("submittedUsername", username != null ? username : "");
        request.setAttribute("databaseConnected", Boolean.valueOf(DBConnection.isAvailable()));
        forward(request, response, "/views/auth/login.jsp");
    }

    private void showLockedError(
        HttpServletRequest request,
        HttpServletResponse response,
        String username,
        long remainingSeconds
    ) throws ServletException, IOException {
        if (remainingSeconds <= 0L) {
            showLoginError(request, response, username, "Invalid username/email or password.");
            return;
        }

        request.setAttribute("lockRemainingSeconds", Long.valueOf(Math.min(LOCK_SECONDS, Math.max(0L, remainingSeconds))));
        showLoginError(request, response, username, "Your account has been locked for 1 minute.");
    }

    private long getRemainingLockSeconds(User user) {
        if (user == null || user.getLockedUntil() == null) {
            return 0L;
        }

        long remainingMillis = Duration.between(
            LocalDateTime.now(),
            user.getLockedUntil().toLocalDateTime()
        ).toMillis();
        return Math.max(0L, (remainingMillis + 999L) / 1000L);
    }

    private void showLockedError(
        HttpServletRequest request,
        HttpServletResponse response,
        String username,
        User user
    ) throws ServletException, IOException {
        long remainingSeconds = LOCK_SECONDS;
        if (user.getLockedUntil() != null) {
            remainingSeconds = getRemainingLockSeconds(user);
        }

        showLockedError(request, response, username, remainingSeconds);
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
