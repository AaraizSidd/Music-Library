package code.songpackage;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class UploadSongHandler implements HttpHandler {

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
                // Read the JSON payload from the request body
                String json = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), "UTF-8"))
                        .lines()
                        .collect(Collectors.joining());

                // Convert JSON to a Song object
                Gson gson = new Gson();
                Song song = gson.fromJson(json, Song.class);
                System.out.println("Song Title: " + song.getSong_title());
                System.out.println("Song Link: " + song.getSong_link());
                System.out.println("Artist: " + song.getArtist_username());
                System.out.println("Album: " + song.getAlbum_name());
                System.out.println("Genre: " + song.getGenre_name());
                System.out.println("Language: " + song.getLanguage_name());

                // Validate the Song object
                validateSong(song);

                // Insert the song into the database
                SongDataHandling songDataHandling = new SongDataHandling();
                boolean isInserted = songDataHandling.addSong(song);

                if (isInserted) {
                    sendResponse(exchange, "{\"message\":\"Song uploaded successfully!\"}", 200);
                } else {
                    sendResponse(exchange, "{\"message\":\"Failed to upload song. Ensure all related metadata exists in the database.\"}", 400);
                }
            } catch (IllegalArgumentException e) {
                sendResponse(exchange, "{\"error\":\"Invalid input: " + e.getMessage() + "\"}", 400);
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, "{\"error\":\"An error occurred while uploading the song.\"}", 500);
            }
        }
    }

    private void validateSong(Song song) {
        if (song.getSong_title() == null || song.getSong_title().isEmpty() ||
                song.getAlbum_name() == null || song.getAlbum_name().isEmpty() ||
                song.getArtist_username() == null || song.getArtist_username().isEmpty() ||
                song.getLanguage_name() == null || song.getLanguage_name().isEmpty() ||
                song.getGenre_name() == null || song.getGenre_name().isEmpty() ||
                song.getSong_link() == null || song.getSong_link().isEmpty()) {
            throw new IllegalArgumentException("All fields are required.");
        }
    }

    // This is the final sendResponse method that takes the response message and status code.
    private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
}
