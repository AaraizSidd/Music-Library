package code.generalpackage;

import code.userpackage.UserService;
import code.artistpackage.ArtistService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class DeleteInfoHandler implements HttpHandler {

    private final UserService userService = new UserService();
    private final ArtistService artistService = new ArtistService();
    private static String username;
    private static String role;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Set CORS headers
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        // Handle preflight requests
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        // Ensure the method is DELETE
        if (!"DELETE".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            return;
        }

        // Read and validate the request body
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        System.out.println("Received request body: " + requestBody);

        if (requestBody == null || requestBody.trim().isEmpty()) {
            sendResponse(exchange, 400, "Missing request body.");
            return;
        }

        try {
            JsonObject requestJson = JsonParser.parseString(requestBody).getAsJsonObject();
            if (requestJson.has("username") && requestJson.has("role")) {
                username = requestJson.get("username").getAsString();
                role = requestJson.get("role").getAsString();
            } else {
                sendResponse(exchange, 400, "Missing required fields: username or role.");
                return;
            }
        } catch (Exception e) {
            sendResponse(exchange, 400, "Invalid JSON format.");
            return;
        }
        System.out.println("Username: " + username);
        System.out.println("Role: " + role);
        // Delete account based on role
        boolean deleted;
        if ("artist".equalsIgnoreCase(role)) {
            deleted = artistService.deleteArtist(username);
        } else if ("user".equalsIgnoreCase(role)) {
            deleted = userService.deleteUser(username);
        } else {
            sendResponse(exchange, 400, "Invalid role specified.");
            return;
        }

        // Respond to client
        if (deleted) {
            sendResponse(exchange, 200, "Account deleted successfully.");
        } else {
            sendResponse(exchange, 404, "Account not found.");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
