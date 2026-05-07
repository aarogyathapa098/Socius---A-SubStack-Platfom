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

@WebServlet("/register")
public class RegisterController extends BaseController {

    private final AuthService authService = new AuthService();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        request.setAttribute("showSidebar", Boolean.FALSE);
        request.setAttribute("pageTitle", "Create Account");
        forward(request, response, "/views/auth/register.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String displayName = trim(request.getParameter("displayName"));
        String username = trim(request.getParameter("username"));
        String email = trim(request.getParameter("email"));
        String phoneNumber = trim(request.getParameter("phoneNumber"));
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        request.setAttribute("showSidebar", Boolean.FALSE);
        request.setAttribute("pageTitle", "Create Account");
        request.setAttribute("submittedDisplayName", displayName);
        request.setAttribute("submittedUsername", username);
        request.setAttribute("submittedEmail", email);
        request.setAttribute("submittedPhoneNumber", phoneNumber);

        if (!ValidationUtil.hasLengthBetween(displayName, 2, 100)) {
            request.setAttribute("registerError", "Enter your name using 2-100 characters.");
            forward(request, response, "/views/auth/register.jsp");
            return;
        }

        if (!ValidationUtil.isValidUsername(username)) {
            request.setAttribute(
                "registerError",
                "Enter a valid username using 3-30 letters, numbers, or underscores."
            );
            forward(request, response, "/views/auth/register.jsp");
            return;
        }

        if (userDAO.getUserByUsername(username) != null) {
            request.setAttribute("registerError", "Username already exists.");
            forward(request, response, "/views/auth/register.jsp");
            return;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            request.setAttribute("registerError", "Enter a valid email address.");
            forward(request, response, "/views/auth/register.jsp");
            return;
        }

        if (userDAO.getUserByEmail(email) != null) {
            request.setAttribute("registerError", "Email exists. Please login.");
            forward(request, response, "/views/auth/register.jsp");
            return;
        }

        if (!ValidationUtil.isValidPhoneNumber(phoneNumber)) {
            request.setAttribute("registerError", "Enter a valid phone number.");
            forward(request, response, "/views/auth/register.jsp");
            return;
        }

        if (userDAO.getUserByPhoneNumber(phoneNumber) != null) {
            request.setAttribute("registerError", "That phone number is already in use.");
            forward(request, response, "/views/auth/register.jsp");
            return;
        }

        if (!ValidationUtil.isStrongPassword(password)) {
            request.setAttribute(
                "registerError",
                "Enter a strong password with at least 8 characters, one uppercase letter, one number, and one symbol."
            );
            forward(request, response, "/views/auth/register.jsp");
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("registerError", "Password and confirmation password must match.");
            forward(request, response, "/views/auth/register.jsp");
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setPasswordHash(authService.hashPassword(password));
        user.setDisplayName(displayName);
        user.setBio("");
        user.setRole("member");

        int userId = userDAO.insertUser(user);

        if (userId <= 0) {
            request.setAttribute("registerError", "The account could not be created. Please try again.");
            forward(request, response, "/views/auth/register.jsp");
            return;
        }

        request.getSession().setAttribute(
            "flashSuccess",
            "Account created successfully. You can sign in now."
        );
        redirect(request, response, "/login");
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
