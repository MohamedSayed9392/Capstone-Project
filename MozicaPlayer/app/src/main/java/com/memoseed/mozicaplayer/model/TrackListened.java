package com.memoseed.mozicaplayer.model;

/**
 * Created by MohamedSayed on 8/7/2017.
 */

public class TrackListened {
    long id,listened;

    public TrackListened(long id, long listened) {
        this.id = id;
        this.listened = listened;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getListened() {
        return listened;
    }

    public void setListened(long listened) {
        this.listened = listened;
    }
}
