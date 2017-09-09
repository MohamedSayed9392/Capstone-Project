package com.memoseed.mozicaplayer.model;

/**
 * Created by MohamedSayed on 8/7/2017.
 */

public class TrackAlbumArt {
    long id;
    String albumArt;

    public TrackAlbumArt(long id, String  albumArt) {
        this.id = id;
        this.albumArt = albumArt;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }
}
