package code.playlistpackage;

import databaseconnector.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDataHandling {

    public boolean insertPlaylist(Playlist playlist) {
        String query = "INSERT INTO playlists (playlist_name, user_id, playlist_createddate) "
                + "VALUES (?, (SELECT user_id FROM users WHERE user_username = ?), ?)";

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, playlist.getPlaylist_name());
            preparedStatement.setString(2, playlist.getUser_username());
            preparedStatement.setDate(3, new java.sql.Date(System.currentTimeMillis())); // Convert String to SQL Date

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0; // Return true if the insert was successful

        } catch (SQLException e) {
            System.out.println("Error while inserting playlist: " + e.getMessage());
            return false;
        }
    }

    public boolean isPlaylistExists(String playlistName, String username) {
        String query = "SELECT COUNT(*) AS count "
                + "FROM playlists p "
                + "JOIN users u ON p.user_id = u.user_id "
                + "WHERE p.playlist_name = ? AND u.user_username = ?";

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, playlistName);
            preparedStatement.setString(2, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("count") > 0; // Return true if a record exists
            }

        } catch (SQLException e) {
            System.out.println("Error while checking for existing playlist: " + e.getMessage());
        }

        return false; // Return false if an error occurs
    }

    public boolean deletePlaylist(String playlistName, String username) {
        String query = "DELETE FROM playlists "
                + "WHERE playlist_name = ? AND user_id = (SELECT user_id FROM users WHERE user_username = ?)";

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, playlistName);
            preparedStatement.setString(2, username);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0; // Return true if the delete was successful

        } catch (SQLException e) {
            System.out.println("Error while deleting playlist: " + e.getMessage());
            return false;
        }
    }

    public List<Playlist> getPlaylistsByUsername(String username) {
        List<Playlist> playlists = new ArrayList<>();
        String query = "SELECT p.playlist_name, u.user_username, p.playlist_createddate "
                + "FROM playlists p "
                + "JOIN users u ON p.user_id = u.user_id "
                + "WHERE u.user_username = ?";

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String playlistName = resultSet.getString("playlist_name");
                String userUsername = resultSet.getString("user_username");
                String createdDate = resultSet.getDate("playlist_createddate").toString(); // Convert SQL Date to String

                playlists.add(new Playlist(playlistName, userUsername, createdDate));
            }

        } catch (SQLException e) {
            System.out.println("Error while retrieving playlists: " + e.getMessage());
        }

        return playlists;
    }
}
