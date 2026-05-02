package service;

import java.time.LocalDateTime;

import dao.UserDAO;
import model.User;
import util.PasswordUtil;
import util.ValidationUtil;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    public boolean isLoginInputValid(String usernameOrEmail, String password) {
        return ValidationUtil.isPresent(usernameOrEmail) && ValidationUtil.isPresent(password);
    }

    public boolean isRegistrationValid(
        String username,
        String email,
        String phoneNumber,
        String password,
        String confirmPassword
    ) {
        return ValidationUtil.isValidUsername(username)
            && ValidationUtil.isValidEmail(email)
            && ValidationUtil.isValidPhoneNumber(phoneNumber)
            && ValidationUtil.isStrongPassword(password)
            && password.equals(confirmPassword);
    }

    public boolean isLocked(User user) {
        return user != null
            && user.getLockedUntil() != null
            && user.getLockedUntil().toLocalDateTime().isAfter(LocalDateTime.now());
    }

    public String hashPassword(String rawPassword) {
        return PasswordUtil.hash(rawPassword);
    }

    public User authenticate(String usernameOrEmail, String password) {
        if (!isLoginInputValid(usernameOrEmail, password)) {
            return null;
        }

        User user = usernameOrEmail.contains("@")
            ? userDAO.getUserByEmail(usernameOrEmail)
            : userDAO.getUserByUsername(usernameOrEmail);

        if (user == null || !user.isActive() || user.isGloballyBanned()) {
            return null;
        }

        if (isLocked(user)) {
            return null;
        }

        return PasswordUtil.verify(password, user.getPasswordHash()) ? user : null;
    }
}
