package code.getpackage;

import code.generalpackage.SignInUserHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import databaseconnector.DatabaseConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import com.google.gson.Gson;

public class GetAlbumsHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Add CORS headers to the response
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        // Handle OPTIONS request (preflight for CORS)
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1); // No Content response
            return;
        }

        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            try (Connection connection = DatabaseConnection.getConnection()) {
                // Query to fetch genres
                String query = "SELECT album_title FROM albums where artist_id = (select artist_id from artists where artist_username = '"+SignInUserHandler.getLOGGED_IN_USER()+"')";
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();

                // Collect genres into a list
                ArrayList<String> albums = new ArrayList<>();
                while (resultSet.next()) {
                    albums.add(resultSet.getString("album_title"));
                }

                // Convert genres list to JSON
                Gson gson = new Gson();
                String response = gson.toJson(albums);

                // Send JSON response
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
                String errorResponse = "Error fetching genres: " + e.getMessage();
                exchange.sendResponseHeaders(500, errorResponse.length());
                OutputStream os = exchange.getResponseBody();
                os.write(errorResponse.getBytes());
                os.close();
            }
        } else {
            // Respond with Method Not Allowed for non-GET requests
            exchange.sendResponseHeaders(405, -1);
        }
    }
}