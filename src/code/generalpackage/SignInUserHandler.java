package code.generalpackage;

import code.generalpackage.SignInResponse.UserCredential;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import databaseconnector.DatabaseConnection;

import java.io.*;
import java.sql.*;
import java.util.stream.Collectors;

public class SignInUserHandler implements HttpHandler {
    private static String LOGGED_IN_USER;

    public static String getLOGGED_IN_USER() {
        return LOGGED_IN_USER;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Set CORS headers for all requests
        setCORSHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            // Handle preflight request (204 No Content response)
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            handleSignInRequest(exchange);
        } else {
            // Method not allowed
            String response = "Method Not Allowed";
            exchange.sendResponseHeaders(405, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    private void handleSignInRequest(HttpExchange exchange) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8"); BufferedReader reader = new BufferedReader(isr)) {

            // Read JSON request
            String json = reader.lines().collect(Collectors.joining());
            System.out.println("Received sign-in data: " + json);

            // Convert JSON to User object
            UserCredential user = new Gson().fromJson(json, UserCredential.class);
            System.out.println("Username: " + user.getUser_username());
            LOGGED_IN_USER = user.getUser_username();
            System.out.println("Password: " + user.getUser_password());
            System.out.println("Role: " + user.getRole());

            // Validate user credentials and retrieve full name if valid
            String value = validateUser(user);

            if (value != null) {
                String[] values = value.split(",");
                if (values.length == 2) {
                    String fullName = values[0];
                    String username = values[1];
                    System.out.println("Fullname: " + fullName);
                    System.out.println("Username: " + username);

                    // Create a response object
                    SignInResponse response = new SignInResponse("Login successful", fullName, username);
                    String jsonResponse = new Gson().toJson(response);

                    sendResponse(exchange, 200, jsonResponse);
                } else {
                    sendResponse(exchange, 500, "Invalid data format returned by validateUser");
                }
            } else {
                sendResponse(exchange, 401, "Invalid username/email or password");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "An error occurred: " + e.getMessage());
        }
    }

    private String validateUser(UserCredential user) {
        String query = user.getRole().equals("user")
                ? "SELECT user_full_name FROM users WHERE (user_username = ? OR user_email = ?) AND user_pass = ?"
                : "SELECT artist_name FROM artists WHERE (artist_username = ? OR artist_email = ?) AND artist_pass = ?";
        String query1 = user.getRole().equals("user")
                ? "SELECT user_username FROM users WHERE (user_username = ? OR user_email = ?) AND user_pass = ?"
                : "SELECT artist_username FROM artists WHERE (artist_username = ? OR artist_email = ?) AND artist_pass = ?";

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(query); PreparedStatement stmt1 = connection.prepareStatement(query1)) {

            stmt1.setString(1, user.getUser_username());
            stmt1.setString(2, user.getUser_username()); // Check both username and email
            stmt1.setString(3, user.getUser_password());
            stmt.setString(1, user.getUser_username());
            stmt.setString(2, user.getUser_username()); // Check both username and email
            stmt.setString(3, user.getUser_password());

            try (ResultSet rs = stmt.executeQuery()) {
                ResultSet rs1 = stmt1.executeQuery();
                if (rs.next() && rs1.next()) {
                    // Return the full name of the user or artist
                    return rs.getString(1) + "," + rs1.getString(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Invalid credentials
    }

// Helper class for JSON response
    private void setCORSHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}

class SignInResponse {

    private final String message;
    private final String name;
    private final String username;

    public SignInResponse(String message, String name, String username) {
        this.message = message;
        this.name = name;
        this.username = username;
    }

    class UserCredential {

        @SerializedName("username")
        private String user_username;

        @SerializedName("password")
        private String user_password;

        @SerializedName("role")
        private String role;

        public String getUser_username() {
            return user_username;
        }

        public String getUser_password() {
            return user_password;
        }

        public String getRole() {
            return role;
        }

        public void setUser_username(String user_username) {
            this.user_username = user_username;
        }

        public void setUser_password(String user_password) {
            this.user_password = user_password;
        }

        public void setRole(String role) {
            this.role = role;
        }
        
    }
}
