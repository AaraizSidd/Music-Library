package code.playlistpackage;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class CreatePlaylistHandler implements HttpHandler {

    private final PlaylistDataHandling playlistDataHandling;
    private final Gson gson;

    public CreatePlaylistHandler() {
        this.playlistDataHandling = new PlaylistDataHandling();
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Handle CORS preflight request
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // Allow all origins
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS"); // Allowed methods
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type"); // Allowed headers
            exchange.sendResponseHeaders(204, -1); // No content for OPTIONS
            return;
        }

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            try (InputStream inputStream = exchange.getRequestBody(); OutputStream outputStream = exchange.getResponseBody()) {

                // Parse JSON data from request body
                Reader reader = new InputStreamReader(inputStream);
                JsonObject jsonRequest = gson.fromJson(reader, JsonObject.class);

                // Extract values from JSON
                String playlistName = jsonRequest.get("playlist_name").getAsString();
                String username = jsonRequest.get("user_username").getAsString();

                // Check if the playlist already exists
                boolean exists = playlistDataHandling.isPlaylistExists(playlistName, username);

                // Prepare response
                Map<String, String> responseMap = new HashMap<>();
                int responseCode;

                if (exists) {
                    responseMap.put("message", "Playlist already exists");
                    responseCode = 409; // Conflict
                } else {
                    // Create a Playlist object
                    Playlist playlist = new Playlist(playlistName, username, "CURDATE");

                    // Insert playlist into the database
                    boolean isInserted = playlistDataHandling.insertPlaylist(playlist);

                    if (isInserted) {
                        responseMap.put("message", "Playlist created successfully");
                        responseCode = 200; // OK
                    } else {
                        responseMap.put("message", "Failed to create playlist");
                        responseCode = 500; // Internal Server Error
                    }
                }

                // Convert response to JSON
                String jsonResponse = gson.toJson(responseMap);
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(responseCode, jsonResponse.length());
                outputStream.write(jsonResponse.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
                String errorMessage = gson.toJson(Map.of("message", "An error occurred while processing the request"));
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(500, errorMessage.length());
                exchange.getResponseBody().write(errorMessage.getBytes());
            }
        } else {
            String methodNotAllowedMessage = gson.toJson(Map.of("message", "Method not allowed"));
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(405, methodNotAllowedMessage.length());
            exchange.getResponseBody().write(methodNotAllowedMessage.getBytes());
        }
    }
}
