package com.example.btl_app_music.Object;

import android.net.Uri;

import java.io.Serializable;

public class MusicList implements Serializable {
    private String title, artist, duration;
    private boolean isPlaying;
    private Uri musicFile;
    private String path;

    public MusicList() {
    }

    public MusicList(String title, String artist, String duration, String path) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.isPlaying = false;
        this.path = path;
    }


    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getDuration() {
        return duration;
    }

    public boolean isPlaying() {
        return isPlaying;
    }



    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
