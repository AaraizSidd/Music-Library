package code.getpackage;

import code.songpackage.Song;
import code.songpackage.SongDataHandling;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetSongs implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, "{\"error\": \"Method Not Allowed\"}", 405);
            return;
        }

        // Parse query parameters to get the artist username
        String query = exchange.getRequestURI().getQuery();
        String[] querys = query.split("&");
        System.out.println("Query: " + query);
        System.out.println("Query1: " + querys[0] + "\nQuery2: "+ querys[1]);
        String username = querys[0].split("=")[1];
        String role=querys[1].split("=")[1];
        
        if (username.isEmpty()) {
            sendResponse(exchange, "{\"error\": \"Username is required.\"}", 400);
            return;
        }
        if (role.isEmpty()) {
            sendResponse(exchange, "{\"error\": \"Role is required.\"}", 400);
            return;
        }

        // Get user role and fetch appropriate songs
        SongDataHandling songDataHandling = new SongDataHandling();
        List<Song> songs;

        if ("artist".equalsIgnoreCase(role)) {
            // Fetch only the songs by the artist
            songs = songDataHandling.getSongsByArtistUsername(username);
        } else if ("user".equalsIgnoreCase(role)) {
            // Fetch all songs for regular users
            songs = songDataHandling.getAllSongs();
        } else {
            sendResponse(exchange, "{\"error\": \"Invalid user role.\"}", 403);
            return;
        }

        // Prepare the response map
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("username", username);
        responseMap.put("role", role);
        responseMap.put("songs", songs);

        // Convert the map to JSON
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(responseMap);
        System.out.println("Sent");
        // Send the response
        sendResponse(exchange, jsonResponse, 200);
    }

    // Method to send response to the client
    private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // Allow all origins
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

}
