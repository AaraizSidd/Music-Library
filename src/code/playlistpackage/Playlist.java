package code.playlistpackage;


public class Playlist {
    
    private String playlist_name;
    private String user_username;
    private String playlist_createddate;
    

    public Playlist(String playlist_name, String user_username, String playlist_createddate) {
        this.playlist_name = playlist_name;
        this.user_username = user_username;
        this.playlist_createddate = playlist_createddate;
    }

    public String getPlaylist_name() {
        return playlist_name;
    }

    public String getUser_username() {
        return user_username;
    }

    public String getPlaylist_createddate() {
        return playlist_createddate;
    }

    public void setPlaylist_name(String playlist_name) {
        this.playlist_name = playlist_name;
    }

    public void setUser_username(String user_username) {
        this.user_username = user_username;
    }

    public void setPlaylist_createddate(String playlist_createddate) {
        this.playlist_createddate = playlist_createddate;
    }
    
    
}
