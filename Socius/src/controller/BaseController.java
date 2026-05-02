package controller;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public abstract class BaseController extends HttpServlet {

    protected void forward(HttpServletRequest request, HttpServletResponse response, String view)
        throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(view);
        dispatcher.forward(request, response);
    }

    protected void redirect(HttpServletRequest request, HttpServletResponse response, String path)
        throws IOException {
        response.sendRedirect(request.getContextPath() + path);
    }
}
