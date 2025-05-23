package databaseconnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/SCHEMA_NAME or DATABASE_NAME";// Replace with your database name
    private static final String USER = "DATABASE_USER_NAME"; // Replace with your database username
    private static final String PASSWORD = "DATABASE_USER_PASSWORD"; // Replace with your database user password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
