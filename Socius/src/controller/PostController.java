package controller;

import java.io.IOException;

import dao.CommentDAO;
import dao.PostDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Post;

@WebServlet("/post")
public class PostController extends BaseController {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        request.setAttribute("showSidebar", Boolean.FALSE);

        String postIdParam = request.getParameter("id");
        Integer postId = parsePostId(postIdParam);

        if (postId == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            request.setAttribute("pageTitle", "Post Not Found");
            request.setAttribute("postNotFound", Boolean.TRUE);
            forward(request, response, "/views/public/post.jsp");
            return;
        }

        PostDAO postDAO = new PostDAO();
        Post post = postDAO.getPostById(postId.intValue());

        if (post == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            request.setAttribute("pageTitle", "Post Not Found");
            request.setAttribute("postNotFound", Boolean.TRUE);
            forward(request, response, "/views/public/post.jsp");
            return;
        }

        CommentDAO commentDAO = new CommentDAO();
        request.setAttribute("pageTitle", post.getTitle());
        request.setAttribute("post", post);
        request.setAttribute("comments", commentDAO.getCommentsByPost(post.getPostId()));
        forward(request, response, "/views/public/post.jsp");
    }

    private Integer parsePostId(String postIdParam) {
        if (postIdParam == null || postIdParam.trim().isEmpty()) {
            return null;
        }

        try {
            return Integer.valueOf(Integer.parseInt(postIdParam.trim()));
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
