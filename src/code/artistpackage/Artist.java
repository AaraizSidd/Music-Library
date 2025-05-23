package code.artistpackage;

public class Artist {

    private String artist_name;

    private String artist_username;
    private String artist_email;
    private String artist_country;
    private String artist_debutyear;
    private String genre_name;
    private String artist_datejoined;
    private String artist_password;
    private int genreId;
    private String role = "artist";
    private int songs;
    private int albums;

    public Artist(String artist_name, String artist_username, String artist_email, String artist_country, String artist_debutyear, String genre_name, String artist_datejoined, String artist_password, int songs, int albums) {
        this.artist_name = artist_name;
        this.artist_username = artist_username;
        this.artist_email = artist_email;
        this.artist_country = artist_country;
        this.artist_debutyear = artist_debutyear;
        this.genre_name = genre_name;
        this.artist_datejoined = artist_datejoined;
        this.artist_password = artist_password;
        this.songs = songs;
        this.albums = albums;
    }

    public Artist(String artist_name, String artist_username, String artist_email, String artist_country, String artist_debutyear, String genre_name, String artist_datejoined) {
        this.artist_name = artist_name;
        this.artist_username = artist_username;
        this.artist_email = artist_email;
        this.artist_country = artist_country;
        this.artist_debutyear = artist_debutyear;
        this.genre_name = genre_name;
        this.artist_datejoined = artist_datejoined;
    }

    public void setSongs(int songs) {
        this.songs = songs;
    }

    public void setAlbums(int albums) {
        this.albums = albums;
    }

    public int getSongs() {
        return songs;
    }

    public int getAlbums() {
        return albums;
    }

    public void setArtist_datejoined(String artist_datejoined) {
        this.artist_datejoined = artist_datejoined;
    }

    public String getArtist_datejoined() {
        return artist_datejoined;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public String getArtist_username() {
        return artist_username;
    }

    public String getArtist_email() {
        return artist_email;
    }

    public String getArtist_country() {
        return artist_country;
    }

    public String getArtist_debutyear() {
        return artist_debutyear;
    }

    public String getGenre_name() {
        return genre_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public void setArtist_username(String artist_username) {
        this.artist_username = artist_username;
    }

    public void setArtist_email(String artist_email) {
        this.artist_email = artist_email;
    }

    public void setArtist_country(String artist_country) {
        this.artist_country = artist_country;
    }

    public void setArtist_debutyear(String artist_debutyear) {
        this.artist_debutyear = artist_debutyear;
    }

    public void setGenre_name(String genre_name) {
        this.genre_name = genre_name;
    }

    public Artist() {
    }

    public Artist(String artistName, String artistUsername, String artistPassword, String artistEmail,
            String artistDebutYear, String artistCountry, String genre_name, String datejoined) {
        this.artist_name = artistName;
        this.artist_username = artistUsername;
        this.artist_password = artistPassword;
        this.artist_email = artistEmail;
        this.artist_debutyear = artistDebutYear;
        this.artist_country = artistCountry;
        this.genre_name = genre_name;
        this.artist_datejoined = datejoined;
    }

//    public Artist(String artistName, String artistUsername, String artistPassword, String artistEmail,
//            String artistDebutYear, String artistCountry, String genreId, String datejoined) {
//        this.artist_name = artistName;
//        this.artist_username = artistUsername;
//        this.artist_password = artistPassword;
//        this.artist_email = artistEmail;
//        this.artist_debutyear = artistDebutYear;
//        this.artist_country = artistCountry;
//        this.genreId = Integer.parseInt(genreId);
//        this.artist_datejoined = datejoined;
//    }
    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public String getArtist_password() {
        return artist_password;
    }

    public void setArtist_password(String artist_password) {
        this.artist_password = artist_password;
    }

    public int getGenreId() {
        return genreId;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    @Override
    public String toString() {
        return "{ " + "artistName = " + artist_name + "  |  artistUsername = " + artist_username
                + "  |  artistEmail = " + artist_email + "  |  artistDebutYear = " + artist_debutyear
                + "  |  artistCountry = " + artist_country + " }";
    }
}
