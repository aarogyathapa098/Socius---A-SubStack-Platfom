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

@WebServlet("/search-suggestions")
public class SearchSuggestionController extends BaseController {

    private static final int RESULT_LIMIT = 5;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String query = trim(request.getParameter("q"));

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        if (query == null || query.length() < 2) {
            response.getWriter().write("{\"communities\":[],\"posts\":[]}");
            return;
        }

        List<Community> communities = new CommunityDAO().searchCommunities(query, RESULT_LIMIT);
        List<Post> posts = new PostDAO().searchApprovedPosts(query, RESULT_LIMIT);

        StringBuilder json = new StringBuilder();
        json.append("{\"communities\":[");
        for (int index = 0; index < communities.size(); index++) {
            Community community = communities.get(index);
            if (index > 0) {
                json.append(',');
            }
            json.append('{')
                .append("\"title\":\"").append(escapeJson(community.getName())).append("\",")
                .append("\"subtitle\":\"").append(escapeJson(community.getMemberCount() + " members")).append("\",")
                .append("\"url\":\"").append(escapeJson(request.getContextPath() + "/community?slug=" + community.getSlug())).append("\"")
                .append('}');
        }
        json.append("],\"posts\":[");
        for (int index = 0; index < posts.size(); index++) {
            Post post = posts.get(index);
            if (index > 0) {
                json.append(',');
            }
            json.append('{')
                .append("\"title\":\"").append(escapeJson(post.getTitle())).append("\",")
                .append("\"subtitle\":\"").append(escapeJson(post.getCommunityName() + " by " + post.getAuthorUsername())).append("\",")
                .append("\"url\":\"").append(escapeJson(request.getContextPath() + "/post?id=" + post.getPostId())).append("\"")
                .append('}');
        }
        json.append("]}");

        response.getWriter().write(json.toString());
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        StringBuilder escaped = new StringBuilder();
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            switch (character) {
                case '"':
                    escaped.append("\\\"");
                    break;
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '\b':
                    escaped.append("\\b");
                    break;
                case '\f':
                    escaped.append("\\f");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    if (character < 0x20) {
                        escaped.append(String.format("\\u%04x", Integer.valueOf(character)));
                    } else {
                        escaped.append(character);
                    }
                    break;
            }
        }
        return escaped.toString();
    }
}
