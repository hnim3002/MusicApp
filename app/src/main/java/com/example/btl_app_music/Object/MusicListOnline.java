package com.example.btl_app_music.Object;

public class MusicListOnline {
    private String title, artist, duration;
    private String path;

    public MusicListOnline() {
    }

    public MusicListOnline(String artist, String duration, String path, String title) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


}
