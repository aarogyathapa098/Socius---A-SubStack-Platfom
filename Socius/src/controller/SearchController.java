package controller;

import java.io.IOException;
import java.util.List;

import dao.CommunityDAO;
import dao.PostDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Community;
import model.Post;

@WebServlet("/search")
public class SearchController extends BaseController {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String query = trim(request.getParameter("q"));
        CommunityDAO communityDAO = new CommunityDAO();
        PostDAO postDAO = new PostDAO();

        request.setAttribute("showSidebar", Boolean.FALSE);
        request.setAttribute("pageTitle", "Search");
        request.setAttribute("query", query);

        if (query == null || query.length() < 2) {
            request.setAttribute("communities", communityDAO.getTrendingCommunities(8));
            request.setAttribute("posts", postDAO.getTrendingPosts(10));
        } else {
            List<Community> communities = communityDAO.searchCommunities(query, 12);
            List<Post> posts = postDAO.searchApprovedPosts(query, 20);
            request.setAttribute("communities", communities);
            request.setAttribute("posts", posts);
        }

        forward(request, response, "/views/public/search.jsp");
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
