package code.generalpackage;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import databaseconnector.DatabaseConnection;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class CreateAlbumHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Set CORS headers
        setCORSHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1); // Handle preflight request
            return;
        }

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            handleCreateAlbumRequest(exchange);
        } else {
            String response = "Method Not Allowed";
            exchange.sendResponseHeaders(405, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    private void handleCreateAlbumRequest(HttpExchange exchange) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
             BufferedReader reader = new BufferedReader(isr)) {

            // Read JSON request
            String json = reader.lines().collect(Collectors.joining());
            System.out.println("Received album data: " + json);

            // Convert JSON to Album object
            Album album = new Gson().fromJson(json, Album.class);

            // Store album in the database
            if (storeAlbumInDatabase(album)) {
                String response = new Gson().toJson(new ApiResponse("Album created successfully"));
                sendResponse(exchange, 200, response);
            } else {
                String response = new Gson().toJson(new ApiResponse("Failed to create album"));
                sendResponse(exchange, 500, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String response = new Gson().toJson(new ApiResponse("An error occurred: " + e.getMessage()));
            sendResponse(exchange, 500, response);
        }
    }

    private boolean storeAlbumInDatabase(Album album) {
        String query = "INSERT INTO albums (album_title, album_releasedate, artist_id) " +
                       "VALUES (?, ?, (SELECT artist_id FROM artists WHERE artist_username = ?))";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, album.getAlbumTitle());
            stmt.setString(2, album.getAlbumReleaseDate());
            stmt.setString(3, album.getUsername());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setCORSHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}

class Album {
    private String albumTitle;
    private String albumReleaseDate;
    private String username;

    public String getAlbumTitle() {
        return albumTitle;
    }

    public String getAlbumReleaseDate() {
        return albumReleaseDate;
    }

    public String getUsername() {
        return username;
    }
}

// Helper class for API responses
class ApiResponse {
    private final String message;

    public ApiResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
