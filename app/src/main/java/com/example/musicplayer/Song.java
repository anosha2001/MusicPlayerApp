package com.example.musicplayer;

public class Song {
    private String id;  // Ensure you have a unique ID for each song
    private String songName;
    private String artistName;
    private String songPath;
    private String songThumbnail;
    private boolean isLiked;  // Like status

    // Getters and setters for the fields
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getSongPath() {
        return songPath;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    public String getSongThumbnail() {
        return songThumbnail;
    }

    public void setSongThumbnail(String songThumbnail) {
        this.songThumbnail = songThumbnail;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }
}
