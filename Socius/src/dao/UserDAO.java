package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    }

    public void updateResetToken(int userId, String resetToken) {
    }

    public void incrementFailedAttempts(int userId) {
    }

    public void resetFailedAttempts(int userId) {
    }

    public void lockAccount(int userId) {
    }

    public void updatePenaltyPoints(int userId, int penaltyPoints) {
    }

    public void setGlobalBan(int userId, boolean banned) {
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
