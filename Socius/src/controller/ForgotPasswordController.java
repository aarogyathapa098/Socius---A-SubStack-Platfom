package controller;

import java.io.IOException;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import service.AuthService;
import util.ValidationUtil;

@WebServlet({"/forgot-password", "/change-password"})
public class ForgotPasswordController extends BaseController {

    private final AuthService authService = new AuthService();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        if ("/forgot-password".equals(request.getServletPath())) {
            redirect(request, response, "/change-password");
            return;
        }

        request.setAttribute("showSidebar", Boolean.FALSE);
        request.setAttribute("pageTitle", "Change Password");
        forward(request, response, "/views/auth/change-password.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String usernameOrEmail = trim(request.getParameter("usernameOrEmail"));
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        request.setAttribute("showSidebar", Boolean.FALSE);
        request.setAttribute("pageTitle", "Change Password");
        request.setAttribute("submittedUsernameOrEmail", usernameOrEmail);

        User user = usernameOrEmail != null && usernameOrEmail.contains("@")
            ? userDAO.getUserByEmail(usernameOrEmail)
            : userDAO.getUserByUsername(usernameOrEmail);

        if (user == null) {
            request.setAttribute("passwordError", "No account found for that username or email.");
            forward(request, response, "/views/auth/change-password.jsp");
            return;
        }

        if (!ValidationUtil.isStrongPassword(newPassword)) {
            request.setAttribute(
                "passwordError",
                "Enter a strong password with at least 8 characters, one uppercase letter, one number, and one symbol."
            );
            forward(request, response, "/views/auth/change-password.jsp");
            return;
        }

        if (confirmPassword == null || !newPassword.equals(confirmPassword)) {
            request.setAttribute("passwordError", "Password and confirmation password must match.");
            forward(request, response, "/views/auth/change-password.jsp");
            return;
        }

        userDAO.updatePassword(user.getUserId(), authService.hashPassword(newPassword));
        request.getSession().setAttribute(
            "flashSuccess",
            "Password changed successfully. You can sign in now."
        );
        redirect(request, response, "/login");
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
