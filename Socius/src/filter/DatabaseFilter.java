package filter;

import java.io.IOException;

import dao.DBConnection;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter("/*")
public class DatabaseFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        if (isStaticPath(path) || DBConnection.isAvailable()) {
            chain.doFilter(request, response);
            return;
        }

        request.setAttribute("pageTitle", "Database Offline");
        request.setAttribute(
            "databaseErrorMessage",
            DBConnection.getLastErrorMessage() != null
                ? "Socius could not prepare the database schema: " + DBConnection.getLastErrorMessage()
                : "Socius requires a working MySQL connection before dynamic pages can be used."
        );
        httpResponse.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        request.getRequestDispatcher("/views/public/database-error.jsp").forward(request, response);
    }

    private boolean isStaticPath(String path) {
        return path.startsWith("/css/")
            || path.startsWith("/js/")
            || path.startsWith("/assets/")
            || path.startsWith("/favicon")
            || path.endsWith(".css")
            || path.endsWith(".js")
            || path.endsWith(".png")
            || path.endsWith(".jpg")
            || path.endsWith(".jpeg")
            || path.endsWith(".svg")
            || path.endsWith(".gif")
            || path.endsWith(".woff")
            || path.endsWith(".woff2");
    }

    @Override
    public void destroy() {
    }
}
