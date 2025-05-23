package code.songpackage;

public class Song {

    private String song_title;
    private String album_name;
    private String artist_username;
    private String artist_name;

    private String language_name;
    private String genre_name;
    private String song_link;

    public Song(String song_title, String album_name, String artist_username, String artist_name, String language_name, String genre_name, String song_link) {
        this.song_title = song_title;
        this.album_name = album_name;
        this.artist_username = artist_username;
        this.artist_name = artist_name;
        this.language_name = language_name;
        this.genre_name = genre_name;
        this.song_link = song_link;
    }

    public Song(String song_title, String album_name, String artist_name, String language_name, String genre_name, String song_link) {
        this.song_title = song_title;
        this.album_name = album_name;
        this.artist_name = artist_name;
        this.language_name = language_name;
        this.genre_name = genre_name;
        this.song_link = song_link;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public String getArtist_name() {
        return artist_name;
    }
    public String getSong_title() {
        return song_title;
    }

    public String getAlbum_name() {
        return album_name;
    }

    public String getArtist_username() {
        return artist_username;
    }

    public String getLanguage_name() {
        return language_name;
    }

    public String getGenre_name() {
        return genre_name;
    }

    public String getSong_link() {
        return song_link;
    }

    public void setSong_title(String song_title) {
        this.song_title = song_title;
    }

    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
    }

    public void setArtist_username(String artist_username) {
        this.artist_username = artist_username;
    }

    public void setLanguage_name(String language_name) {
        this.language_name = language_name;
    }

    public void setGenre_name(String genre_name) {
        this.genre_name = genre_name;
    }

    public void setSong_link(String song_link) {
        this.song_link = song_link;
    }

}
