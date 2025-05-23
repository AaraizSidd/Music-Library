package code.generalpackage;

import code.userpackage.User;
import code.artistpackage.ArtistDataHandling;
import code.artistpackage.Artist;
import code.userpackage.UserDataHandling;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import databaseconnector.DatabaseConnection;

import java.io.*;
import java.sql.*;
import java.util.stream.Collectors;

public class RegisterHandler implements HttpHandler {

    private final UserDataHandling userDataHandling = new UserDataHandling();
    private final ArtistDataHandling artistDataHandling = new ArtistDataHandling();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Allow CORS
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1); // Respond to preflight requests
            return;
        }

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            try {
                // Read the JSON payload
                String json = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), "UTF-8"))
                        .lines()
                        .collect(Collectors.joining());

                Gson gson = new Gson();
                RegistrationData data = gson.fromJson(json, RegistrationData.class);
                System.out.println("Received role: " + data.getRole());
                System.out.println("Email: " + data.getEmail());
                System.out.println("Username: " + data.getUsername());
                System.out.println("Password: " + data.getPassword());

                if (data.getRole().equals("artist")) {
                    System.out.println("Artist Name: " + data.getName());
                    System.out.println("DebutYear: " + data.getDebutyear());
                    System.out.println("Country: " + data.getCountry());
                    System.out.println("Genre: " + data.getGenre());
                    data.setGenre(data.getGenre().substring(0, 1).toUpperCase() + data.getGenre().substring(1).toLowerCase());
                    System.out.println("Genre: " + data.getGenre());
                } else {
                    System.out.println("User First Name: " + data.getFirstname());
                    System.out.println("User Last Name: " + data.getLastname());
                }
                if (data.getRole() == null || data.getRole().isEmpty()) {
                    throw new IllegalArgumentException("Role is missing.");
                } // Process registration based on role
                else if ("artist".equalsIgnoreCase(data.getRole())) {
                    try (Connection connection = DatabaseConnection.getConnection()) {
                        // Query to fetch genres
                        String query = "SELECT genre_id FROM genres WHERE genre_name = ?";
                        PreparedStatement statement = connection.prepareStatement(query);
                        statement.setString(1, data.getGenre());

                        ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            data.setGenre(resultSet.getString("genre_id"));
                        }
                        System.out.println("Genre ID: "+data.getGenre());

                    }
                    registerArtist(data);
                } else if ("user".equalsIgnoreCase(data.getRole())) {
                    registerUser(data);
                } else {
                    throw new IllegalArgumentException("Invalid role specified.");
                }

                String response = "Registration successful!";
                sendResponse(exchange, 200, response);

            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "Error: " + e.getMessage());
            }
        }
    }

    private void registerUser(RegistrationData data) throws SQLException {
        if (data.getFirstname() == null || data.getUsername() == null || data.getPassword() == null || data.getEmail() == null) {
            throw new IllegalArgumentException("Required fields for user registration are missing.");
        }

        User user = new User(data.getUsername(), data.getPassword(), data.getFirstname(), data.getLastname(), data.getEmail(), "CURDATE");
        userDataHandling.saveUser(user);
        System.out.println("User registered successfully.");
    }

    private void registerArtist(RegistrationData data) throws SQLException {
        if (data.getName() == null || data.getUsername() == null || data.getPassword() == null
                || data.getEmail() == null || data.getGenre() == null || data.getDebutyear() == null) {
            throw new IllegalArgumentException("Required fields for artist registration are missing.");
        }

        Artist artist = new Artist(data.getName(), data.getUsername(), data.getPassword(), data.getEmail(), data.getDebutyear(), data.getCountry(), data.getGenre(), "CURDATE");
        artistDataHandling.saveArtist(artist);
        System.out.println("Artist registered successfully.");
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        byte[] responseBytes = message.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }

    static class RegistrationData {

        private String role;
        private String firstname;
        private String lastname;
        private String username;
        private String password;
        private String email;
        private String name; // For artists
        private String genre;
        private String debutyear;
        private String country;

        // Getters and setters
        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGenre() {
            return genre;
        }

        public void setGenre(String genre) {
            this.genre = genre;
        }

        public String getDebutyear() {
            return debutyear;
        }

        public void setDebutyear(String debutyear) {
            this.debutyear = debutyear;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }
}
