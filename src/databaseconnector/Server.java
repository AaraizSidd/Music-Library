package databaseconnector;

import code.generalpackage.CreateAlbumHandler;
import code.generalpackage.DeleteInfoHandler;
import code.getpackage.GetAlbumsHandler;
import code.getpackage.GetGenreHandler;
import code.getpackage.GetInfoHandler;
import code.getpackage.GetLanguageHandler;
import com.sun.net.httpserver.HttpServer;
import code.generalpackage.RegisterHandler;
import code.generalpackage.SignInUserHandler;
import code.generalpackage.UpdateInfoHandler;
import code.getpackage.GetSongs;
import code.playlistpackage.CreatePlaylistHandler;
import code.songpackage.DeleteSongHandler;
import code.songpackage.UpdateSongInfo;
import code.songpackage.UploadSongHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        
        // Register the handler for user registration
        server.createContext("/registerUser", new RegisterHandler());

        // Register the handler for OTP generation and sending
        server.createContext("/otp", new OTPHandler());
        
        // Login the handler for user signing in
        server.createContext("/signInUser", new SignInUserHandler());
        
        server.createContext("/getGenres", new GetGenreHandler());
        
        server.createContext("/getLanguages", new GetLanguageHandler());
        
        server.createContext("/getAlbums", new GetAlbumsHandler());
        
        server.createContext("/createAlbum", new CreateAlbumHandler());
        
        server.createContext("/getInfo", new GetInfoHandler());
        
        server.createContext("/updateInfo", new UpdateInfoHandler());
        
        server.createContext("/deleteAccount", new DeleteInfoHandler());
        
        server.createContext("/uploadSong", new UploadSongHandler());
        
        server.createContext("/getSongs", new GetSongs());
        
        server.createContext("/createPlaylist", new CreatePlaylistHandler());
        
        server.createContext("/updateSong", new UpdateSongInfo());
        
        server.createContext("/deleteSong", new DeleteSongHandler());

        server.setExecutor(null); // Creates a default executor
        System.out.println("Server started on port 8081...");
        server.start();
    }
}
