package code.artistpackage;

import java.sql.SQLException;

public class ArtistService {
    private final ArtistDataHandling artistDataHandling;

    public ArtistService() {
        this.artistDataHandling = new ArtistDataHandling();
    }

    // Creates the artist table
    public void createArtistTable() {
        artistDataHandling.createArtistTable();
    }

    // Saves a new artist record
    public boolean saveArtist(Artist artist) throws SQLException {
        return artistDataHandling.saveArtist(artist);
    }

    // Deletes an artist and their related records
    public boolean deleteArtist(String artistUsername) {
        return artistDataHandling.deleteArtist(artistUsername);
    }
}