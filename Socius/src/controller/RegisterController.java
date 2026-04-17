package controller;

import java.io.IOException;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import service.AuthService;

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

        String username = trim(request.getParameter("username"));
        String email = trim(request.getParameter("email"));
        String phoneNumber = trim(request.getParameter("phoneNumber"));
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        request.setAttribute("showSidebar", Boolean.FALSE);
        request.setAttribute("pageTitle", "Create Account");
        request.setAttribute("submittedUsername", username);
        request.setAttribute("submittedEmail", email);
        request.setAttribute("submittedPhoneNumber", phoneNumber);

        if (!authService.isRegistrationValid(username, email, phoneNumber, password, confirmPassword)) {
            request.setAttribute(
                "registerError",
                "Enter a valid username, email, phone number, and a strong password that matches confirmation."
            );
            forward(request, response, "/views/auth/register.jsp");
            return;
        }

        if (userDAO.getUserByUsername(username) != null) {
            request.setAttribute("registerError", "That username is already in use.");
            forward(request, response, "/views/auth/register.jsp");
            return;
        }

        if (userDAO.getUserByEmail(email) != null) {
            request.setAttribute("registerError", "That email address is already in use.");
            forward(request, response, "/views/auth/register.jsp");
            return;
        }

        if (userDAO.getUserByPhoneNumber(phoneNumber) != null) {
            request.setAttribute("registerError", "That phone number is already in use.");
            forward(request, response, "/views/auth/register.jsp");
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setPasswordHash(authService.hashPassword(password));
        user.setDisplayName(username);
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
