package controller;

import java.io.IOException;
import java.util.List;

import dao.CommunityDAO;
import dao.PostDAO;
import dao.ReportDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Community;
import model.Post;

@WebServlet({"/discover", "/about", "/contact"})
public class DiscoverController extends BaseController {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String servletPath = request.getServletPath();
        String view = "/views/public/discover.jsp";
        String pageTitle = "Discover";
        request.setAttribute("showSidebar", Boolean.FALSE);

        if ("/about".equals(servletPath)) {
            view = "/views/public/about.jsp";
            pageTitle = "About";
        } else if ("/contact".equals(servletPath)) {
            view = "/views/public/contact.jsp";
            pageTitle = "Contact";
        } else {
            loadDiscoverData(request);
        }

        request.setAttribute("pageTitle", pageTitle);
        forward(request, response, view);
    }

    private void loadDiscoverData(HttpServletRequest request) {
        CommunityDAO communityDAO = new CommunityDAO();
        PostDAO postDAO = new PostDAO();
        ReportDAO reportDAO = new ReportDAO();

        try {
            int communityCount = communityDAO.getTotalCommunityCount();
            int approvedPostCount = postDAO.getApprovedPostCount();
            int pendingPostCount = postDAO.getPendingPostCount();
            int openReportCount = reportDAO.getOpenReportCount();
            List<Community> featuredCommunities = communityDAO.getAllCommunities(6, 0);
            List<Post> recentPosts = postDAO.getRecentApprovedPosts(6);

            request.setAttribute("databaseConnected", Boolean.TRUE);
            request.setAttribute("communityCount", Integer.valueOf(communityCount));
            request.setAttribute("approvedPostCount", Integer.valueOf(approvedPostCount));
            request.setAttribute("pendingPostCount", Integer.valueOf(pendingPostCount));
            request.setAttribute("openReportCount", Integer.valueOf(openReportCount));
            request.setAttribute("featuredCommunities", featuredCommunities);
            request.setAttribute("recentPosts", recentPosts);

            if (!recentPosts.isEmpty()) {
                request.setAttribute("featurePost", recentPosts.get(0));
            }
        } catch (RuntimeException exception) {
            throw exception;
        }
    }
}
