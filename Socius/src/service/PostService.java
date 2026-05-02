package service;

import model.Post;
import model.User;
import util.ValidationUtil;

public class PostService {

    public boolean isPostValid(Post post) {
        return post != null
            && ValidationUtil.hasLengthBetween(post.getTitle(), 5, 300)
            && (
                ValidationUtil.hasLengthBetween(post.getContent(), 10, 20000)
                    || post.hasImage()
            );
    }

    public String resolveInitialStatus(User user, boolean requiresReview) {
        if (user == null) {
            return "pending";
        }

        String role = user.getRole();
        if ("admin".equals(role) || "moderator".equals(role) || !requiresReview) {
            return "approved";
        }

        return "pending";
    }
}
