package controller;

import java.io.IOException;
import java.util.List;

import dao.CommunityDAO;
import dao.CommunityMembershipDAO;
import dao.PostDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Community;
import model.Post;
import model.User;

@WebServlet("/community")
public class CommunityController extends BaseController {

    private static final int COMMUNITY_PAGE_SIZE = 24;
    private static final int COMMUNITY_POST_LIMIT = 20;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        CommunityDAO communityDAO = new CommunityDAO();
        PostDAO postDAO = new PostDAO();
        String slug = trimToNull(request.getParameter("slug"));
        String keyword = trimToNull(request.getParameter("keyword"));

        request.setAttribute("showSidebar", Boolean.FALSE);

        if (slug != null) {
            Community community = communityDAO.getApprovedCommunityBySlug(slug);

            if (community == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                request.setAttribute("pageTitle", "Community Not Found");
                request.setAttribute("communityNotFound", Boolean.TRUE);
                request.setAttribute("communities", communityDAO.getAllCommunities(COMMUNITY_PAGE_SIZE, 0));
                forward(request, response, "/views/public/community.jsp");
                return;
            }

            List<Post> communityPosts = postDAO.getApprovedPostsByCommunity(
                community.getCommunityId(),
                COMMUNITY_POST_LIMIT,
                0
            );

            request.setAttribute("pageTitle", community.getName());
            request.setAttribute("selectedCommunity", community);
            request.setAttribute("communityPosts", communityPosts);
            request.setAttribute(
                "approvedPostCount",
                Integer.valueOf(postDAO.getApprovedPostCountByCommunity(community.getCommunityId()))
            );
            request.setAttribute(
                "communityJoined",
                Boolean.valueOf(isCommunityJoined(request.getSession(false), community.getCommunityId()))
            );
            forward(request, response, "/views/public/community.jsp");
            return;
        }

        List<Community> communities = keyword == null
            ? communityDAO.getAllCommunities(COMMUNITY_PAGE_SIZE, 0)
            : communityDAO.searchCommunities(keyword);

        request.setAttribute("pageTitle", "Communities");
        request.setAttribute("communities", communities);
        request.setAttribute("keyword", keyword);
        forward(request, response, "/views/public/community.jsp");
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean isCommunityJoined(HttpSession session, int communityId) {
        if (session == null) {
            return false;
        }

        Object currentUser = session.getAttribute("currentUser");
        if (!(currentUser instanceof User)) {
            return false;
        }

        return new CommunityMembershipDAO().isMember(((User) currentUser).getUserId(), communityId);
    }
}
