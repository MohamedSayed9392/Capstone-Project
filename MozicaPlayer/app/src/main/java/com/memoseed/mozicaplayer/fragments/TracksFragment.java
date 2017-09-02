package com.memoseed.mozicaplayer.fragments;

/**
 * Created by MemoSeed on 03/08/2017.
 */

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.memoseed.mozicaplayer.R;
import com.memoseed.mozicaplayer.activities.MainActivity_;
import com.memoseed.mozicaplayer.adapters.LibraryRVAdapter;
import com.memoseed.mozicaplayer.database.DatabaseHandler;
import com.memoseed.mozicaplayer.database.TracksContentProvider;
import com.memoseed.mozicaplayer.model.Track;
import com.memoseed.mozicaplayer.model.TrackListened;
import com.memoseed.mozicaplayer.utils.Music;
import com.memoseed.mozicaplayer.utils.UTils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EFragment(R.layout.fragment_all_tracks)
public class TracksFragment extends Fragment {

    String TAG = getClass().getSimpleName();

    public LibraryRVAdapter libraryRVAdapter;

    public static TracksFragment_ newInstance(int currentTab) {
        TracksFragment_ tracksFragment = new TracksFragment_();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("currentTab", currentTab);
        tracksFragment.setArguments(args);

        return tracksFragment;
    }

    int currentTab;
    public DatabaseHandler databaseHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentTab = getArguments().getInt("currentTab");
        Log.d(TAG, "currentTab : " + currentTab);
        databaseHandler = new DatabaseHandler(mContext);
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
    public RecyclerView rView;
    public LinearLayoutManager linearLayoutManager;

    @AfterViews
    void afterViews() {
        libraryRVAdapter = new LibraryRVAdapter(mContext,currentTab);
        linearLayoutManager = new LinearLayoutManager(mContext);
        rView.setLayoutManager(new LinearLayoutManager(mContext));
        rView.setAdapter(libraryRVAdapter);
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
