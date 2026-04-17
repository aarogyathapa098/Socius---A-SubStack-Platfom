package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {

    private static final String URL =
        "jdbc:mysql://localhost:3306/socius_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static boolean driverLoaded;

    private DBConnection() {
    }

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            driverLoaded = true;
        } catch (ClassNotFoundException exception) {
            driverLoaded = false;
        }
    }

    public static Connection getConnection() throws SQLException {
        if (!driverLoaded) {
            throw new SQLException("MySQL JDBC driver not found.");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static boolean isAvailable() {
        try (Connection ignored = getConnection()) {
            return true;
        } catch (SQLException exception) {
            return false;
        }
    }
}
