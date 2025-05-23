package code.songpackage;

import databaseconnector.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SongDataHandling {

    // Insert a new song with names and titles
    public boolean addSong(Song song) {
        String findAlbumIdQuery = "SELECT album_id FROM albums WHERE album_title = ?";
        String findArtistIdQuery = "SELECT artist_id FROM artists WHERE artist_username = ?";
        String findGenreIdQuery = "SELECT genre_id FROM genres WHERE genre_name = ?";
        String findLanguageIdQuery = "SELECT language_id FROM languages WHERE language_name = ?";
        String insertSongQuery = "INSERT INTO songs (song_title, album_id, artist_id, genre_id, language_id, song_link) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection()) {

            // Fetch Album ID
            int albumId = fetchId(connection, findAlbumIdQuery, song.getAlbum_name());
            if (albumId == -1) {
                System.out.println("Album not found.");
                return false;
            }

            // Fetch Artist ID
            int artistId = fetchId(connection, findArtistIdQuery, song.getArtist_username());
            if (artistId == -1) {
                System.out.println("Artist not found.");
                return false;
            }

            // Fetch Genre ID
            int genreId = fetchId(connection, findGenreIdQuery, song.getGenre_name());
            if (genreId == -1) {
                System.out.println("Genre not found.");
                return false;
            }

            // Fetch Language ID
            int languageId = fetchId(connection, findLanguageIdQuery, song.getLanguage_name());
            if (languageId == -1) {
                System.out.println("Language not found.");
                return false;
            }

            try (PreparedStatement statement = connection.prepareStatement(insertSongQuery)) {
                statement.setString(1, song.getSong_title());
                statement.setInt(2, albumId);
                statement.setInt(3, artistId);
                statement.setInt(4, genreId);
                statement.setInt(5, languageId);
                statement.setString(6, song.getSong_link());

                return statement.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateSong(Song song) {
        String findAlbumIdQuery = "SELECT album_id FROM albums WHERE album_title = ?";
        String findArtistIdQuery = "SELECT artist_id FROM artists WHERE artist_username = ?";
        String findGenreIdQuery = "SELECT genre_id FROM genres WHERE genre_name = ?";
        String findLanguageIdQuery = "SELECT language_id FROM languages WHERE language_name = ?";
        String updateSongQuery = """
        UPDATE songs 
        SET song_title = ?, album_id = ?, artist_id = ?, genre_id = ?, language_id = ?, song_link = ? 
        WHERE song_id = (SELECT temp.song_id 
                         FROM (SELECT song_id FROM songs WHERE song_title = ?) AS temp)
        """;

        try (Connection connection = DatabaseConnection.getConnection()) {

            // Fetch Album ID
            int albumId = fetchId(connection, findAlbumIdQuery, song.getAlbum_name());
            if (albumId == -1) {
                System.out.println("Album not found.");
                return false;
            }

            // Fetch Artist ID
            int artistId = fetchId(connection, findArtistIdQuery, song.getArtist_username());
            if (artistId == -1) {
                System.out.println("Artist not found.");
                return false;
            }

            // Fetch Genre ID
            int genreId = fetchId(connection, findGenreIdQuery, song.getGenre_name());
            if (genreId == -1) {
                System.out.println("Genre not found.");
                return false;
            }

            // Fetch Language ID
            int languageId = fetchId(connection, findLanguageIdQuery, song.getLanguage_name());
            if (languageId == -1) {
                System.out.println("Language not found.");
                return false;
            }

            // Update Song
            try (PreparedStatement statement = connection.prepareStatement(updateSongQuery)) {
                statement.setString(1, song.getSong_title());
                statement.setInt(2, albumId);
                statement.setInt(3, artistId);
                statement.setInt(4, genreId);
                statement.setInt(5, languageId);
                statement.setString(6, song.getSong_link());
                statement.setString(7, song.getSong_title());

                return statement.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteSong(String song_title) {
        String selectSongIdQuery = "SELECT song_id FROM songs WHERE song_title = ?";
        String deleteSongQuery = "DELETE FROM songs WHERE song_id = ?";
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement selectStatement = connection.prepareStatement(selectSongIdQuery); PreparedStatement deleteStatement = connection.prepareStatement(deleteSongQuery)) {

            // Step 1: Get song_id
            selectStatement.setString(1, song_title);
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                int songId = resultSet.getInt("song_id");

                // Step 2: Delete the song
                deleteStatement.setInt(1, songId);
                return deleteStatement.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get songs by artist username
    public List<Song> getSongsByArtistUsername(String artistUsername) {
        String query = "SELECT s.song_title, a.album_title, ar.artist_name, l.language_name, g.genre_name, s.song_link "
                + "FROM songs s "
                + "JOIN albums a ON s.album_id = a.album_id "
                + "JOIN artists ar ON s.artist_id = ar.artist_id "
                + "JOIN languages l ON s.language_id = l.language_id "
                + "JOIN genres g ON s.genre_id = g.genre_id "
                + "WHERE ar.artist_username = ?";

        List<Song> songs = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, artistUsername);

            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Executing query for artist username: " + artistUsername);
                while (resultSet.next()) {
                    String songTitle = resultSet.getString("song_title");
                    String albumTitle = resultSet.getString("album_title");
                    String artistName = resultSet.getString("artist_name");
                    String languageName = resultSet.getString("language_name");
                    String genreName = resultSet.getString("genre_name");
                    String songLink = resultSet.getString("song_link");

                    songs.add(new Song(songTitle, albumTitle, artistName, languageName, genreName, songLink));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songs;
    }

    // Get all songs
    public List<Song> getAllSongs() {
        String query = "SELECT s.song_title, a.album_title, ar.artist_name, l.language_name, g.genre_name, s.song_link "
                + "FROM songs s "
                + "JOIN albums a ON s.album_id = a.album_id "
                + "JOIN artists ar ON s.artist_id = ar.artist_id "
                + "JOIN languages l ON s.language_id = l.language_id "
                + "JOIN genres g ON s.genre_id = g.genre_id";

        List<Song> songs = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String songTitle = resultSet.getString("song_title");
                String albumTitle = resultSet.getString("album_title");
                String artistName = resultSet.getString("artist_name");
                String languageName = resultSet.getString("language_name");
                String genreName = resultSet.getString("genre_name");
                String songLink = resultSet.getString("song_link");

                songs.add(new Song(songTitle, albumTitle, artistName, languageName, genreName, songLink));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songs;
    }

    // Helper method to fetch IDs by name/title
    private int fetchId(Connection connection, String query, String name) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1); // Return the ID
                }
            }
        }
        return -1; // ID not found
    }
}
