package controller;

import java.io.IOException;

import dao.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.User;
import service.AuthService;

@WebServlet("/login")
public class LoginController extends BaseController {

    private final AuthService authService = new AuthService();

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

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        User user = authService.authenticate(username, password);

        if (user == null) {
            request.setAttribute("showSidebar", Boolean.FALSE);
            request.setAttribute("pageTitle", "Sign In");
            request.setAttribute("loginError", "Invalid username/email or password.");
            request.setAttribute("submittedUsername", username != null ? username.trim() : "");
            request.setAttribute("databaseConnected", Boolean.valueOf(DBConnection.isAvailable()));
            forward(request, response, "/views/auth/login.jsp");
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute("currentUser", user);

        redirect(request, response, "/member/home");
    }
}
