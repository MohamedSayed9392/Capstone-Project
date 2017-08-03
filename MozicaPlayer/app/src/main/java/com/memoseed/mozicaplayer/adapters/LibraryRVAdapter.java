package com.memoseed.mozicaplayer.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.memoseed.mozicaplayer.model.Track;

import java.text.SimpleDateFormat;
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

    SimpleDateFormat formatDATE = new SimpleDateFormat("dd/MM/yyyy");

    public LibraryRVAdapter(Context con, List<Track> Tracks) {
        this.Tracks = Tracks;
        this.con = con;
        formatDATE.setTimeZone(TimeZone.getDefault());
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
        holder.txtAdded.setText(formatDATE.format(track.getAdded()));
        holder.txtDuration.setText(new SimpleDateFormat("mm:ss").format(new Date(track.getDuration())));
        holder.txtListened.setText(String.valueOf(track.getListened()));

        holder.linItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(con,track.getFileName(),Toast.LENGTH_LONG).show();
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


}
