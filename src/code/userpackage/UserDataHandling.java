package code.userpackage;

import databaseconnector.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;

public class UserDataHandling {

    public void createUserTable() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS artists (
                user_id INT AUTO_INCREMENT PRIMARY KEY,
                user_firstname VARCHAR(100) NOT NULL,
                user_lastname VARCHAR(100) NOT NULL,
                user_username VARCHAR(50) NOT NULL UNIQUE,
                user_pass VARCHAR(100) NOT NULL,
                user_email VARCHAR(100) NOT NULL,
                user_full_name VARCHAR(200) NOT NULL GENERATED ALWAYS AS (concat(firstname,' ',lastname)),
                user_datejoined DATE NOT NULL
                );
        """;

        try (Connection connection = DatabaseConnection.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
            System.out.println("Users table created or already exists.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean saveUser(User user) throws SQLException {
        if (user.getUser_username() == null || user.getUser_password() == null || user.getUser_email() == null) {
            throw new IllegalArgumentException("Required fields are missing.");
        }

        String query = "INSERT INTO users (user_username, user_pass, user_firstname, user_lastname, user_email, user_datejoined) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, user.getUser_username());
            stmt.setString(2, user.getUser_password());
            stmt.setString(3, user.getUser_firstname());
            stmt.setString(4, user.getUser_lastname());
            stmt.setString(5, user.getUser_email());
            stmt.setDate(6, new java.sql.Date(System.currentTimeMillis())); // Store current date
            int rowsAffected = stmt.executeUpdate();
            System.out.println("User saved successfully.");
            return rowsAffected > 0; // Return true if rows were affected
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false on failure
        }
    }

    public ArrayList<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();
        String selectSQL = "SELECT * FROM users";

        try (Connection connection = DatabaseConnection.getConnection(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(selectSQL)) {

            while (resultSet.next()) {
                User user = new User(
                        resultSet.getString("user_username"),
                        resultSet.getString("user_pass"),
                        resultSet.getString("user_firstname"),
                        resultSet.getString("user_lastname"),
                        resultSet.getString("user_email"),
                        resultSet.getString("user_datejoined")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public boolean deleteUser(String userName) {
        String deleteSQL = "DELETE FROM users WHERE user_username = ?";
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
            preparedStatement.setString(1, userName);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User deleted successfully.");
                return true; // Return true if a user was deleted
            } else {
                System.out.println("User not found.");
                return false; // Return false if no user was found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false on error
        }
    }

    public User getUserByUsername(String username) {
        String query = "SELECT * FROM users WHERE user_username = ?";
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getString("user_username"),
                            rs.getString("user_pass"),
                            rs.getString("user_firstname"),
                            rs.getString("user_lastname"),
                            rs.getString("user_email"),
                            rs.getDate("user_datejoined").toString()
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUserInfo(String username, String email, String firstName, String lastName) {
        String updateSQL = "UPDATE users SET user_email = ?, user_firstname = ?, user_lastname = ? WHERE user_username = ?";
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(updateSQL)) {
            stmt.setString(1, email);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, username);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
