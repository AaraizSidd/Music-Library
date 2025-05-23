package code.songpackage;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class DeleteSongHandler implements HttpHandler {

    private final SongDataHandling songDataHandling;
    private final Gson gson;

    public DeleteSongHandler() {
        this.songDataHandling = new SongDataHandling();
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            // Respond to preflight request
            exchange.sendResponseHeaders(204, -1); // No Content
            return;
        }

        if (!"DELETE".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        if (query == null || !query.contains("title=")) {
            sendResponse(exchange, 400, "Bad Request: Missing 'title' parameter");
            return;
        }

        String songTitle = query.split("title=")[1].trim();
        if (songTitle.isEmpty()) {
            sendResponse(exchange, 400, "Bad Request: 'title' parameter is empty");
            return;
        }

        boolean isDeleted = songDataHandling.deleteSong(songTitle);

        if (isDeleted) {
            sendResponse(exchange, 200, "Song deleted successfully.");
        } else {
            sendResponse(exchange, 404, "Song not found or could not be deleted.");
        }
    }

    private void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/plain");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
