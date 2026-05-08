package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.User;

public class UserDAO {

    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load user by ID.", exception);
        }

        return null;
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load user by username.", exception);
        }

        return null;
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load user by email.", exception);
        }

        return null;
    }

    public User getUserByPhoneNumber(String phoneNumber) {
        String sql = "SELECT * FROM users WHERE phone_number = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, phoneNumber);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load user by phone number.", exception);
        }

        return null;
    }

    public int insertUser(User user) {
        String sql =
            "INSERT INTO users (username, email, phone_number, password_hash, display_name, bio, role) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPhoneNumber());
            statement.setString(4, user.getPasswordHash());
            statement.setString(5, user.getDisplayName());
            statement.setString(6, user.getBio());
            statement.setString(7, user.getRole() != null ? user.getRole() : "member");
            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to insert user.", exception);
        }

        return 0;
    }

    public void updateProfile(int userId, String displayName, String bio, String phoneNumber) {
        String sql = "UPDATE users SET display_name = ?, bio = ?, phone_number = ? WHERE user_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, displayName);
            statement.setString(2, bio);
            statement.setString(3, phoneNumber);
            statement.setInt(4, userId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to update profile.", exception);
        }
    }

    public void updateRole(int userId, String role) {
        String sql = "UPDATE users SET role = ? WHERE user_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, role);
            statement.setInt(2, userId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to update user role.", exception);
        }
    }

    public void updatePassword(int userId, String passwordHash) {
        String sql = "UPDATE users SET password_hash = ?, reset_token = NULL, reset_token_expires_at = NULL WHERE user_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, passwordHash);
            statement.setInt(2, userId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to update password.", exception);
        }
    }

    public void updateResetToken(int userId, String resetToken) {
    }

    public void incrementFailedAttempts(int userId) {
        String sql = "UPDATE users SET failed_attempts = failed_attempts + 1 WHERE user_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to update failed login attempts.", exception);
        }
    }

    public void resetFailedAttempts(int userId) {
        String sql = "UPDATE users SET failed_attempts = 0, locked_until = NULL WHERE user_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to reset failed login attempts.", exception);
        }
    }

    public void lockAccount(int userId) {
        String sql = "UPDATE users SET failed_attempts = 5, locked_until = ? WHERE user_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now().plusMinutes(1)));
            statement.setInt(2, userId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to lock account.", exception);
        }
    }

    public void updatePenaltyPoints(int userId, int penaltyPoints) {
        String sql = "UPDATE users SET penalty_points = ? WHERE user_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, penaltyPoints);
            statement.setInt(2, userId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to update penalty points.", exception);
        }
    }

    public void setGlobalBan(int userId, boolean banned) {
        String sql =
            "UPDATE users SET is_globally_banned = ?, is_active = ?, failed_attempts = 0, locked_until = NULL "
                + "WHERE user_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setBoolean(1, banned);
            statement.setBoolean(2, !banned);
            statement.setInt(3, userId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to update global ban state.", exception);
        }
    }

    public void updateAdminUser(
        int userId,
        String username,
        String displayName,
        String email,
        String phoneNumber,
        String role,
        int penaltyPoints,
        boolean active,
        boolean globallyBanned
    ) {
        String sql =
            "UPDATE users SET username = ?, display_name = ?, email = ?, phone_number = ?, role = ?, "
                + "penalty_points = ?, is_active = ?, is_globally_banned = ? WHERE user_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, username);
            statement.setString(2, displayName);
            statement.setString(3, email);
            statement.setString(4, phoneNumber);
            statement.setString(5, role);
            statement.setInt(6, penaltyPoints);
            statement.setBoolean(7, active);
            statement.setBoolean(8, globallyBanned);
            statement.setInt(9, userId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to update user from admin panel.", exception);
        }
    }

    public List<User> getAllUsers(int limit, int offset) {
        List<User> users = new ArrayList<User>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC LIMIT ? OFFSET ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, limit);
            statement.setInt(2, offset);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    users.add(mapUser(resultSet));
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load users.", exception);
        }

        return users;
    }

    public List<User> searchUsers(String keyword) {
        return new ArrayList<User>();
    }

    public int getTotalUserCount() {
        String sql = "SELECT COUNT(*) FROM users";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to count users.", exception);
        }

        return 0;
    }

    public List<User> getRecentUsers(int limit) {
        List<User> users = new ArrayList<User>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC LIMIT ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    users.add(mapUser(resultSet));
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load recent users.", exception);
        }

        return users;
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setUserId(resultSet.getInt("user_id"));
        user.setUsername(resultSet.getString("username"));
        user.setEmail(resultSet.getString("email"));
        user.setPhoneNumber(resultSet.getString("phone_number"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setDisplayName(resultSet.getString("display_name"));
        user.setBio(resultSet.getString("bio"));
        user.setAvatarUrl(resultSet.getString("avatar_url"));
        user.setRole(resultSet.getString("role"));
        user.setPenaltyPoints(resultSet.getInt("penalty_points"));
        user.setWarningCount(resultSet.getInt("warning_count"));
        user.setActive(resultSet.getBoolean("is_active"));
        user.setGloballyBanned(resultSet.getBoolean("is_globally_banned"));
        user.setFailedAttempts(resultSet.getInt("failed_attempts"));
        user.setLockedUntil(resultSet.getTimestamp("locked_until"));
        user.setResetToken(resultSet.getString("reset_token"));
        user.setResetTokenExpiresAt(resultSet.getTimestamp("reset_token_expires_at"));
        user.setCreatedAt(resultSet.getTimestamp("created_at"));
        return user;
    }
}
