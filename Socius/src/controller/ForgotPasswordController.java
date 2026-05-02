package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/forgot-password")
public class ForgotPasswordController extends BaseController {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        request.setAttribute("pageTitle", "Forgot Password");
        forward(request, response, "/views/auth/forgot-password.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        request.getSession().setAttribute(
            "flashSuccess",
            "Password reset wiring is not finished yet, but the flow endpoint is now in place."
        );
        redirect(request, response, "/login");
    }
}
