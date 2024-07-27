package com.depec.depechuancayosur;

import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;

public class Song {
    private String title;
    private String artist;
    private int resId;
    private int duration;
    private int imageResource; // Nuevo campo para el recurso de imagen
    private String filePath;

    public Song(String title, String artist, int resId, int duration, int imageResource) {
        this.title = title;
        this.artist = artist;
        this.resId = resId;
        this.duration = duration;
        this.imageResource = imageResource;
        this.filePath = filePath;

    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public int getResId() {
        return resId;
    }

    public int getDuration() {
        return duration;
    }

    public int getImageResource() {
        return imageResource;
    }






}

