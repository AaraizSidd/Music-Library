package databaseconnector;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class OTPHandler implements HttpHandler {

    // In OTPHandler
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            handleCors(exchange);
            return;
        }

        // Handle other requests (POST, GET)
        if ("POST".equals(exchange.getRequestMethod())) {
            handlePostRequest(exchange);
        } else if ("GET".equals(exchange.getRequestMethod())) {
            handleGetRequest(exchange);
        } else {
            sendResponse(exchange, 405, "Method Not Allowed");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*"); // Set CORS headers for all responses
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8); BufferedReader reader = new BufferedReader(isr)) {

            // Read and log incoming data
            String body = reader.lines().collect(Collectors.joining());
            System.out.println("Request received: " + body);

            // Parse JSON data
            Gson gson = new Gson();
            Map<String, String> requestData = gson.fromJson(body, Map.class);

            // Validate required fields
            String email = requestData.get("email");
            String role = requestData.get("roledata");
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("Email is required.");
            }
            System.out.println("Parsed email: " + email);
            System.out.println("Parsed role: " + role);

            try (Connection connection = DatabaseConnection.getConnection()) {
                // Query to check if the email exists in artists and users
                String artistQuery = "SELECT artist_email FROM artists WHERE artist_email = ?";
                String userQuery = "SELECT user_email FROM users WHERE user_email = ?";
                PreparedStatement artistStmt = connection.prepareStatement(artistQuery);
                PreparedStatement userStmt = connection.prepareStatement(userQuery);

                artistStmt.setString(1, email);
                userStmt.setString(1, email);

                ResultSet artistResultSet = artistStmt.executeQuery();
                ResultSet userResultSet = userStmt.executeQuery();
                if (role.equals("artist")) {
                    if (artistResultSet.next()) {
                        String response = "{\"message\": \"Email already exists\"}";
                        sendResponse(exchange, 200, response);
                    } else {
                        OTPService.sendOtp(email);
                        String response = "{\"message\": \"OTP sent successfully to " + email + "\"}";
                        sendResponse(exchange, 200, response);
                    }
                } else if (role.equals("user")) {
                    if (userResultSet.next()) {
                        String response = "{\"message\": \"Email already exists\"}";
                        sendResponse(exchange, 200, response);
                    } else {
                        OTPService.sendOtp(email);
                        String response = "{\"message\": \"OTP sent successfully to " + email + "\"}";
                        sendResponse(exchange, 200, response);
                    }
                }

            } catch (JsonSyntaxException e) {
                System.err.println("Invalid JSON format: " + e.getMessage());
                sendResponse(exchange, 400, "{\"error\": \"Invalid JSON format.\"}");
            } catch (IllegalArgumentException e) {
                System.err.println("Error: " + e.getMessage());
                sendResponse(exchange, 400, "{\"error\": \"" + e.getMessage() + "\"}");
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"error\": \"Internal server error.\"}");
            }
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        System.out.println("Received query: " + query);

        if (query == null || !query.contains("email") || !query.contains("otp")) {
            sendResponse(exchange, 400, "{\"error\": \"Invalid query parameters.\"}");
            return;
        }

        Map<String, String> queryParams = parseQueryParams(query);

        String email = queryParams.get("email");
        String otp = queryParams.get("otp");

        if (email == null || otp == null) {
            sendResponse(exchange, 400, "{\"error\": \"Email and OTP are required.\"}");
            return;
        }

        System.out.println("Parsed email: " + email);
        System.out.println("Parsed OTP: " + otp);

        try {
            // Verify OTP (assume OTPService.verifyOtp is implemented correctly)
            boolean isOtpValid = OTPService.verifyOtp(email, otp);
            if (isOtpValid) {
                sendResponse(exchange, 200, "OTP Verified Successfully");
            } else {
                sendResponse(exchange, 400, "Invalid OTP");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\": \"Internal server error.\"}");
        }
    }

    /**
     * Handles CORS preflight (OPTIONS request).
     */
    private void handleCors(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.sendResponseHeaders(200, -1); // Send a successful response with no body
    }

    /**
     * Parses query parameters into a Map.
     */
    private Map<String, String> parseQueryParams(String query) {
        return Arrays.stream(query.split("&"))
                .map(param -> param.split("=", 2)) // Split each key-value pair
                .collect(Collectors.toMap(
                        pair -> decodeURIComponent(pair[0]), // Decode key
                        pair -> pair.length > 1 ? decodeURIComponent(pair[1]) : "" // Decode value, default to empty string
                ));
    }

    /**
     * Decodes URL-encoded strings.
     */
    private String decodeURIComponent(String encoded) {
        try {
            return java.net.URLDecoder.decode(encoded, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 encoding not supported", e);
        }
    }

}
