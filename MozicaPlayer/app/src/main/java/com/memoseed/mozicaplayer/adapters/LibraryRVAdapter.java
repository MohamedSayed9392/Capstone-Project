package com.memoseed.mozicaplayer.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.memoseed.mozicaplayer.R;
import com.memoseed.mozicaplayer.activities.MainActivity_;
import com.memoseed.mozicaplayer.database.DatabaseHandler;
import com.memoseed.mozicaplayer.database.TracksContentProvider;
import com.memoseed.mozicaplayer.model.Track;
import com.memoseed.mozicaplayer.utils.UTils;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Wayne on 3/27/15.
 */
public class LibraryRVAdapter extends RecyclerView.Adapter<LibraryRVAdapter.View_Holder> {

    String TAG = getClass().getSimpleName();
    private List<Track> Tracks;
    Context con;
    int currentTab;

    SimpleDateFormat formatDATE = new SimpleDateFormat("dd/MM/yyyy");

    public LibraryRVAdapter(Context con, List<Track> Tracks,int currentTab) {
        this.Tracks = Tracks;
        this.con = con;
        formatDATE.setTimeZone(TimeZone.getDefault());
        this.currentTab = currentTab;
    }


    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.item_list_track, parent, false);
        View_Holder holder = new View_Holder(convertView);

        return holder;

    }

    @Override
    public void onBindViewHolder(final View_Holder holder, final int position) {
        Track track = Tracks.get(position);

        holder.txtTitle.setText(track.getTitle());
        holder.txtAdded.setText(formatDATE.format(new Date(track.getAdded()*1000)));
        holder.txtDuration.setText(new SimpleDateFormat("mm:ss").format(new Date(track.getDuration())));
        holder.txtListened.setText(String.valueOf(track.getListened()));

        holder.linItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(con,track.getFileName(),Toast.LENGTH_LONG).show();

                Log.d(TAG,"listened : "+track.getListened());

                track.setListened(track.getListened()+1);

                ContentValues values = new ContentValues();
                values.put("listened", track.getListened());

                Uri contentUri = Uri.withAppendedPath(TracksContentProvider.CONTENT_URI, DatabaseHandler.TABLE_LISTENED_TRACKS);
                if(track.getListened()>1) {
                    con.getContentResolver().update(contentUri, values, "id = ?", new String[]{String.valueOf(track.getId())});
                }else{
                    values.put("id", track.getId());
                    con.getContentResolver().insert(contentUri, values);
                }

                if(position!=0) {
                    if (Tracks.get(position - 1).getListened() < track.getListened()) {
                        swap(position, position-1);
                    }
                }

                notifyItemChanged(position);
            }
        });

    }

    public void addTrack(Track mainItem) {
        this.Tracks.add(mainItem);
        notifyDataSetChanged();
    }




    public void addTracks(List<Track> Tracks) {
        this.Tracks.addAll(Tracks);
        notifyDataSetChanged();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return Tracks.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class View_Holder extends RecyclerView.ViewHolder {

        TextView txtTitle,txtAdded,txtListened,txtDuration;
        ImageView imAlbumArt,imNowPlaying;
        CheckBox checkFav;
        LinearLayout linItem;
        
        View_Holder(View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtAdded = (TextView) itemView.findViewById(R.id.txtAdded);
            txtListened = (TextView) itemView.findViewById(R.id.txtListened);
            txtDuration = (TextView) itemView.findViewById(R.id.txtDuration);
            imAlbumArt = (ImageView) itemView.findViewById(R.id.imAlbumArt);
            imNowPlaying = (ImageView) itemView.findViewById(R.id.imNowPlaying);
            linItem = (LinearLayout) itemView.findViewById(R.id.linItem);
            checkFav = (CheckBox) itemView.findViewById(R.id.checkFav);
        }
    }


    public void swap(int firstPosition, int secondPosition)
    {
        Collections.swap(Tracks, firstPosition, secondPosition);
        notifyItemMoved(firstPosition, secondPosition);
        ((MainActivity_)con).scrollToPosition(secondPosition,currentTab);
    }
}
