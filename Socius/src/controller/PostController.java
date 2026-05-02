package controller;

import java.io.IOException;

import dao.CommentDAO;
import dao.NotificationDAO;
import dao.PostDAO;
import dao.ReportDAO;
import dao.VoteDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Comment;
import model.Post;
import model.Report;
import model.User;
import model.Vote;

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
        User currentUser = getCurrentUser(request.getSession(false));
        Vote currentVote = currentUser != null
            ? new VoteDAO().getVote(currentUser.getUserId(), post.getPostId())
            : null;
        postDAO.incrementViewCount(post.getPostId());

        request.setAttribute("pageTitle", post.getTitle());
        request.setAttribute("post", post);
        request.setAttribute("currentVote", currentVote);
        request.setAttribute("comments", commentDAO.getCommentsByPost(post.getPostId()));
        forward(request, response, "/views/public/post.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        User user = getCurrentUser(request.getSession(false));
        if (user == null) {
            redirect(request, response, "/login");
            return;
        }

        Integer postId = parsePostId(request.getParameter("postId"));
        String action = trim(request.getParameter("action"));

        if (postId == null || action == null) {
            redirect(request, response, "/member/home");
            return;
        }

        PostDAO postDAO = new PostDAO();
        Post post = postDAO.getPostById(postId.intValue());
        if (post == null) {
            redirect(request, response, "/member/home");
            return;
        }

        if ("vote".equals(action)) {
            handleVote(request, user, post);
        } else if ("comment".equals(action)) {
            handleComment(request, user, post);
        } else if ("report".equals(action)) {
            handleReport(request, user, post);
        }

        redirect(request, response, "/post?id=" + post.getPostId());
    }

    private void handleVote(HttpServletRequest request, User user, Post post) {
        String voteType = trim(request.getParameter("voteType"));
        if (!"up".equals(voteType) && !"down".equals(voteType)) {
            request.getSession().setAttribute("flashSuccess", "Choose an upvote or downvote.");
            return;
        }

        VoteDAO voteDAO = new VoteDAO();
        Vote existingVote = voteDAO.getVote(user.getUserId(), post.getPostId());
        int upvotes = post.getUpvotes();
        int downvotes = post.getDownvotes();

        if (existingVote == null) {
            voteDAO.insertVote(user.getUserId(), post.getPostId(), voteType);
            if ("up".equals(voteType)) {
                upvotes++;
            } else {
                downvotes++;
            }
        } else if (voteType.equals(existingVote.getVoteType())) {
            voteDAO.deleteVote(user.getUserId(), post.getPostId());
            if ("up".equals(voteType)) {
                upvotes--;
            } else {
                downvotes--;
            }
        } else {
            voteDAO.updateVote(user.getUserId(), post.getPostId(), voteType);
            if ("up".equals(voteType)) {
                upvotes++;
                downvotes--;
            } else {
                downvotes++;
                upvotes--;
            }
        }
        new PostDAO().updateVoteCount(post.getPostId(), Math.max(0, upvotes), Math.max(0, downvotes));
    }

    private void handleComment(HttpServletRequest request, User user, Post post) {
        String content = trim(request.getParameter("content"));
        Integer parentId = parsePostId(request.getParameter("parentId"));

        if (content == null || content.length() < 2) {
            request.getSession().setAttribute("flashSuccess", "Write a comment before posting.");
            return;
        }

        Comment comment = new Comment();
        comment.setPostId(post.getPostId());
        comment.setAuthorId(user.getUserId());
        comment.setParentId(parentId);
        comment.setContent(content);
        new CommentDAO().insertComment(comment);

        if (post.getAuthorId() != user.getUserId()) {
            new NotificationDAO().createNotification(
                post.getAuthorId(),
                user.getUsername() + " commented on your post: " + post.getTitle(),
                "/post?id=" + post.getPostId()
            );
        }
        request.getSession().setAttribute("flashSuccess", "Comment posted.");
    }

    private void handleReport(HttpServletRequest request, User user, Post post) {
        String reason = trim(request.getParameter("reason"));
        Integer commentId = parsePostId(request.getParameter("commentId"));

        if (reason == null) {
            reason = commentId == null ? "Reported post" : "Reported comment";
        }

        Report report = new Report();
        report.setReporterId(user.getUserId());
        report.setPostId(Integer.valueOf(post.getPostId()));
        report.setCommentId(commentId);
        report.setReason(reason);
        new ReportDAO().insertReport(report);
        request.getSession().setAttribute("flashSuccess", "Report sent to moderators.");
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

    private User getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object currentUser = session.getAttribute("currentUser");
        return currentUser instanceof User ? (User) currentUser : null;
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
