package filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.User;

@WebFilter(urlPatterns = {"/moderator/*", "/admin/*"})
public class RoleFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        if (session == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        Object currentUser = session.getAttribute("currentUser");
        if (!(currentUser instanceof User)) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        User user = (User) currentUser;
        String path = httpRequest.getRequestURI();
        boolean adminArea = path.contains("/admin/");
        boolean moderatorArea = path.contains("/moderator/");

        if (adminArea && !"admin".equals(user.getRole())) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/discover");
            return;
        }

        if (moderatorArea
            && !"admin".equals(user.getRole())
            && !"moderator".equals(user.getRole())) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/discover");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
