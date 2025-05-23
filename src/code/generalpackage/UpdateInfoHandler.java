package code.generalpackage;

import code.userpackage.User;
import code.artistpackage.Artist;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import databaseconnector.DatabaseConnection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class UpdateInfoHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Add CORS headers to allow cross-origin requests
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            // Respond to preflight requests
            exchange.sendResponseHeaders(204, -1); // No Content
            return;
        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            return;
        }

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        int statusCode;

        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            JsonObject requestObject = gson.fromJson(reader, JsonObject.class);

            String role = requestObject.get("role").getAsString();

            try (Connection connection = DatabaseConnection.getConnection()) {
                boolean success;
                if ("user".equalsIgnoreCase(role)) {
                    User user = gson.fromJson(requestObject, User.class);
                    success = updateUser(connection, user);
                } else if ("artist".equalsIgnoreCase(role)) {
                    Artist artist = gson.fromJson(requestObject, Artist.class);
                    success = updateArtist(connection, artist);
                } else {
                    responseObject.addProperty("message", "Invalid role specified.");
                    statusCode = 400; // Bad Request
                    sendResponse(exchange, gson.toJson(responseObject), statusCode);
                    return;
                }

                responseObject.addProperty("message", success ? role + " updated successfully." : "Failed to update " + role + ".");
                statusCode = success ? 200 : 500; // OK or Internal Server Error
            }
        } catch (Exception e) {
            responseObject.addProperty("message", "Error handling update: " + e.getMessage());
            statusCode = 500; // Internal Server Error
        }

        sendResponse(exchange, gson.toJson(responseObject), statusCode);
    }

    private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    private boolean updateUser(Connection connection, User user) {
        String query = "UPDATE users SET user_firstname = ?, user_lastname = ?, user_username = ?, user_pass = ? WHERE user_email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, user.getUser_firstname());
            stmt.setString(2, user.getUser_lastname());
            stmt.setString(3, user.getUser_username());
            stmt.setString(4, user.getUser_password());
            stmt.setString(5, user.getUser_email());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    private boolean updateArtist(Connection connection, Artist artist) {
        String query = "UPDATE artists SET artist_name = ?, artist_username = ?, artist_country = ?, artist_debutyear = ?, genre_id = (select genres.genre_id from genres where genres.genre_name = ? ), artist_pass = ? WHERE artist_email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, artist.getArtist_name());
            stmt.setString(2, artist.getArtist_username());
            stmt.setString(3, artist.getArtist_country());
            stmt.setInt(4, Integer.parseInt(artist.getArtist_debutyear()));
            stmt.setString(5,artist.getGenre_name());
            stmt.setString(6, artist.getArtist_password());
            stmt.setString(7, artist.getArtist_email());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error updating artist: " + e.getMessage());
            return false;
        }
    }

}
