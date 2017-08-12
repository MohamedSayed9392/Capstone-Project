package com.memoseed.mozicaplayer.model;

/**
 * Created by MohamedSayed on 8/3/2017.
 */

public class Track {
    String title,fileName,filePath,albumArt,artist;
    long id,listened,duration,added;
    boolean fav = false;
    long currentTime = 0;

    public Track(String title, String fileName, String filePath,String albumArt,String artist,long id, long listened, long duration, long added,boolean fav) {
        this.title = title;
        this.fileName = fileName;
        this.filePath = filePath;
        this.albumArt = albumArt;
        this.artist = artist;
        this.id = id;
        this.listened = listened;
        this.duration = duration;
        this.added = added;
        this.fav = fav;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getListened() {
        return listened;
    }

    public void setListened(long listened) {
        this.listened = listened;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getAdded() {
        return added;
    }

    public void setAdded(long added) {
        this.added = added;
    }

    public String getAlbumArt() {
        return albumArt;
    }


    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }
}
