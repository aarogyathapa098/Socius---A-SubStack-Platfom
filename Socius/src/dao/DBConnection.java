package dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class DBConnection {

    private static final String URL =
        "jdbc:mysql://localhost:3306/socius_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static boolean driverLoaded;
    private static boolean schemaReady;
    private static String lastErrorMessage;

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

        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        try {
            ensureSchemaReady(connection);
            return connection;
        } catch (SQLException exception) {
            try {
                connection.close();
            } catch (SQLException closeException) {
                exception.addSuppressed(closeException);
            }
            throw exception;
        }
    }

    public static boolean isAvailable() {
        try (Connection ignored = getConnection()) {
            lastErrorMessage = null;
            return true;
        } catch (SQLException exception) {
            lastErrorMessage = exception.getMessage();
            return false;
        }
    }

    public static String getLastErrorMessage() {
        return lastErrorMessage;
    }

    private static synchronized void ensureSchemaReady(Connection connection) throws SQLException {
        if (schemaReady) {
            return;
        }

        if (!tableExists(connection, "posts")) {
            throw new SQLException("Required table `posts` was not found in `socius_db`.");
        }

        runStatement(
            connection,
            "ALTER TABLE posts MODIFY post_type ENUM('text', 'resource', 'event', 'image') DEFAULT 'text'"
        );

        if (!columnExists(connection, "posts", "image_url")) {
            runStatement(connection, "ALTER TABLE posts ADD COLUMN image_url VARCHAR(500) AFTER resource_url");
        }

        if (!columnExists(connection, "posts", "image_alt_text")) {
            runStatement(connection, "ALTER TABLE posts ADD COLUMN image_alt_text VARCHAR(255) AFTER image_url");
        }

        if (!columnExists(connection, "communities", "approval_status")) {
            runStatement(
                connection,
                "ALTER TABLE communities ADD COLUMN approval_status ENUM('pending', 'approved', 'rejected') DEFAULT 'approved' AFTER requires_review"
            );
        }

        if (!tableExists(connection, "notifications")) {
            runStatement(
                connection,
                "CREATE TABLE notifications ("
                    + "notification_id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "user_id INT NOT NULL,"
                    + "message VARCHAR(300) NOT NULL,"
                    + "target_url VARCHAR(500),"
                    + "is_read TINYINT(1) DEFAULT 0,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE"
                    + ")"
            );
        }

        schemaReady = true;
    }

    private static boolean tableExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (
            ResultSet resultSet = metaData.getTables(
                connection.getCatalog(),
                null,
                tableName,
                new String[] { "TABLE" }
            )
        ) {
            return resultSet.next();
        }
    }

    private static boolean columnExists(Connection connection, String tableName, String columnName)
        throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (
            ResultSet resultSet = metaData.getColumns(
                connection.getCatalog(),
                null,
                tableName,
                columnName
            )
        ) {
            return resultSet.next();
        }
    }

    private static void runStatement(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }
}
