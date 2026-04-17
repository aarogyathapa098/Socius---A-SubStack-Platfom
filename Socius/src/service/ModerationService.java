package service;

import model.User;

public class ModerationService {

    public int calculatePenaltyPoints(int warningCount, boolean severeViolation) {
        return severeViolation ? warningCount + 3 : warningCount + 1;
    }

    public boolean shouldLockPosting(User user) {
        return user != null && user.getPenaltyPoints() >= 5;
    }

    public boolean canModerateCommunity(User user) {
        if (user == null) {
            return false;
        }

        return "moderator".equals(user.getRole()) || "admin".equals(user.getRole());
    }
}
