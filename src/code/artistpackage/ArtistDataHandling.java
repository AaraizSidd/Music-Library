package code.artistpackage;

import databaseconnector.DatabaseConnection;
import java.sql.*;

public class ArtistDataHandling {

    public void createArtistTable() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS artists (
                artist_id INT AUTO_INCREMENT PRIMARY KEY,
                artist_name VARCHAR(100) NOT NULL,
                artist_username VARCHAR(50) NOT NULL UNIQUE,
                artist_pass VARCHAR(100) NOT NULL,
                artist_email VARCHAR(100) NOT NULL,
                artist_debutyear INT NOT NULL,
                artist_country VARCHAR(100) NOT NULL,
                genre_id INT NOT NULL,
                artist_datejoined DATE NOT NULL,
                FOREIGN KEY (genre_id) REFERENCES genres(genre_id)
            );
        """;

        try (Connection connection = DatabaseConnection.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
            System.out.println("Artists table created or already exists.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean saveArtist(Artist artist) throws SQLException {
        if (artist.getArtist_username() == null || artist.getArtist_password() == null || artist.getArtist_email() == null) {
            throw new IllegalArgumentException("Required fields are missing.");
        }
        String query = "INSERT INTO artists (artist_name, artist_username, artist_pass, artist_email, artist_debutyear, artist_country, genre_id, artist_datejoined) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, artist.getArtist_name());
            stmt.setString(2, artist.getArtist_username());
            stmt.setString(3, artist.getArtist_password());
            stmt.setString(4, artist.getArtist_email());
            stmt.setInt(5, Integer.parseInt(artist.getArtist_debutyear()));
            stmt.setString(6, artist.getArtist_country());
            stmt.setInt(7, Integer.parseInt(artist.getGenre_name()));
            stmt.setDate(8, new java.sql.Date(System.currentTimeMillis()));

            stmt.executeUpdate();
            System.out.println("Artist saved successfully.");
            int rowsAffected = stmt.executeUpdate();
            System.out.println("User saved successfully.");
            return rowsAffected > 0; // Return true if rows were affected
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false on failure
        }
    }

    public boolean deleteArtist(String artistUsername) {
        String deleteArtistSQL = "DELETE FROM artists WHERE artist_username = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
           

            try (PreparedStatement deleteArtistStmt = connection.prepareStatement(deleteArtistSQL)) {
                // Delete artist
                deleteArtistStmt.setString(1, artistUsername);
                int rowsAffected = deleteArtistStmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Artist and related data deleted successfully.");
                    return true;
                } else {
                    System.out.println("Artist not found.");
                    return false;
                }

            } catch (SQLException e) {
                connection.rollback(); // Rollback transaction in case of error
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
