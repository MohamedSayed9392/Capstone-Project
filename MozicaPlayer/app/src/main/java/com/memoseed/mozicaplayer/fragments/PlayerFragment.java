package com.memoseed.mozicaplayer.fragments;

/**
 * Created by MemoSeed on 03/08/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.memoseed.mozicaplayer.R;
import com.memoseed.mozicaplayer.activities.MainActivity;
import com.memoseed.mozicaplayer.activities.MainActivity_;
import com.memoseed.mozicaplayer.adapters.LibraryRVAdapter;
import com.memoseed.mozicaplayer.database.DatabaseHandler;
import com.memoseed.mozicaplayer.model.Track;
import com.memoseed.mozicaplayer.utils.UTils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EFragment(R.layout.fragment_player)
public class PlayerFragment extends Fragment {

    String TAG = getClass().getSimpleName();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private Activity mContext;

    // called for API equal or above 23
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = (Activity) context;
    }

    /*
* Deprecated on API 23
*/
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @ViewById
    TextView txtTitle;
    @ViewById
    ImageView imAlbumArt;
    @ViewById
    ProgressBar progressBar;
    @ViewById
    Button btnPrev;
    @Click void btnPrev(){

    }
    @ViewById
    CheckBox checkPlay;
    @ViewById
    Button btnNext;
    @Click void btnNext(){

    }
    @ViewById
    TextView txtCurrent;
    @ViewById
    SeekBar seekBar;
    @ViewById
    TextView txtEnd;

    @AfterViews
    void afterViews() {
        txtTitle.setText(MainActivity.getInstance().currentTrack.getTitle());
        txtCurrent.setText(new SimpleDateFormat("mm:ss").format(new Date(MainActivity.getInstance().currentTrack.getCurrentTime())));
        txtEnd.setText(new SimpleDateFormat("mm:ss").format(new Date(MainActivity.getInstance().currentTrack.getDuration())));
        seekBar.setMax((int)MainActivity.getInstance().currentTrack.getDuration());
        seekBar.setProgress((int)MainActivity.getInstance().currentTrack.getCurrentTime());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void toast(String s) {
        Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
    }
}
