package code.getpackage;

import code.userpackage.User;
import code.artistpackage.Artist;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import databaseconnector.DatabaseConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.google.gson.Gson;

public class GetInfoHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Add CORS headers
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        // Handle OPTIONS request
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            String queryParams = exchange.getRequestURI().getQuery();
            if (queryParams == null || !queryParams.contains("username=")) {
                sendResponse(exchange, 400, "Missing or invalid username parameter.");
                return;
            }

            String username = queryParams.split("username=")[1];

            try (Connection connection = DatabaseConnection.getConnection()) {
                String userQuery = "SELECT user_firstname, user_lastname, user_full_name, user_email, user_username, user_datejoined, user_pass FROM users WHERE user_username = ?";
                String artistQuery = "SELECT a.artist_name, a.artist_username, a.artist_email, a.artist_country, a.artist_debutyear, g.genre_name, a.artist_datejoined, a.artist_pass FROM artists a JOIN genres g ON a.genre_id = g.genre_id WHERE a.artist_username = ?";
               
                String artist_idquery = "SELECT artist_id from artists where artist_username= ?";
                int artist_id=0;
                PreparedStatement artist_idStmt = connection.prepareStatement(artist_idquery);
                artist_idStmt.setString(1, username);
                ResultSet artist_idResultSet = artist_idStmt.executeQuery();
                
                if (artist_idResultSet.next()) {
                    artist_id = artist_idResultSet.getInt("artist_id");
                }
                String countAlbums = "SELECT COUNT(*) as total_albums from albums where artist_id= ?";
                String countSongs = "SELECT COUNT(*) as total_songs from songs where artist_id= ?";

                PreparedStatement albumsStmt = connection.prepareStatement(countAlbums);
                albumsStmt.setInt(1, artist_id);
                ResultSet albumsResultSet = albumsStmt.executeQuery();
                
                int albums = 0;
                if (albumsResultSet.next()) {
                    albums = albumsResultSet.getInt("total_albums");
                }
                
                PreparedStatement songsStmt = connection.prepareStatement(countSongs);
                songsStmt.setInt(1, artist_id);
                ResultSet songsResultSet = songsStmt.executeQuery();
                
                int songs = 0;
                if (songsResultSet.next()) {
                    songs = songsResultSet.getInt("total_songs");
                }
// Check if username exists in users table
                PreparedStatement userStmt = connection.prepareStatement(userQuery);
                userStmt.setString(1, username);
                ResultSet userResultSet = userStmt.executeQuery();

                if (userResultSet.next()) {
                    // User found
                    User user = new User(
                            userResultSet.getString("user_username"),
                            userResultSet.getString("user_pass"),
                            userResultSet.getString("user_firstname"),
                            userResultSet.getString("user_lastname"),
                            userResultSet.getString("user_full_name"),
                            userResultSet.getString("user_email"),
                            userResultSet.getString("user_datejoined")
                    );
                    sendResponse(exchange, 200, new Gson().toJson(user));
                } else {
                    // Check if username exists in artists table
                    PreparedStatement artistStmt = connection.prepareStatement(artistQuery);
                    artistStmt.setString(1, username);
                    ResultSet artistResultSet = artistStmt.executeQuery();

                    if (artistResultSet.next()) {
                        // Artist found
                        Artist artist = new Artist(
                                artistResultSet.getString("artist_name"),
                                artistResultSet.getString("artist_username"),
                                artistResultSet.getString("artist_email"),
                                artistResultSet.getString("artist_country"),
                                artistResultSet.getString("artist_debutyear"),
                                artistResultSet.getString("genre_name"),
                                artistResultSet.getString("artist_datejoined"),
                                artistResultSet.getString("artist_pass"),
                                songs,
                                albums
                        );
                        sendResponse(exchange, 200, new Gson().toJson(artist));
                    } else {
                        sendResponse(exchange, 404, "User or artist not found.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "Error fetching info: " + e.getMessage());
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
