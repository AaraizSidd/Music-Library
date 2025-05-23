package code.songpackage;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class UpdateSongInfo implements HttpHandler {

    private final SongDataHandling songDataHandling;

    public UpdateSongInfo() {
        this.songDataHandling = new SongDataHandling();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Add CORS headers to every response
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        // Handle preflight (OPTIONS) requests
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1); // No Content
            return;
        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        try (InputStream requestBody = exchange.getRequestBody()) {
            String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Song updatedSong;

            try {
                updatedSong = gson.fromJson(body, Song.class);
                System.out.println("Username: "+updatedSong.getArtist_username());
                System.out.println("Username: "+updatedSong.getArtist_name());
                System.out.println("Song Title: "+updatedSong.getSong_title());
                System.out.println("Song Link: "+updatedSong.getSong_link());
                System.out.println("Language: "+updatedSong.getLanguage_name());
                System.out.println("Genre: "+updatedSong.getGenre_name());
                System.out.println("Album: "+updatedSong.getAlbum_name());
            } catch (JsonSyntaxException e) {
                sendResponse(exchange, 400, "Invalid JSON format");
                return;
            }

            boolean isUpdated = songDataHandling.updateSong(updatedSong);

            if (isUpdated) {
                sendResponse(exchange, 200, "Song updated successfully");
            } else {
                sendResponse(exchange, 500, "Failed to update song. Please check the input data.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Internal Server Error");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, message.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream responseBody = exchange.getResponseBody()) {
            responseBody.write(message.getBytes(StandardCharsets.UTF_8));
        }
    }
}
