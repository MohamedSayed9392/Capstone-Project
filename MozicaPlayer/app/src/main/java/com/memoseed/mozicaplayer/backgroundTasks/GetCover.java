package com.memoseed.mozicaplayer.backgroundTasks;

import android.os.AsyncTask;

import com.memoseed.mozicaplayer.activities.MainActivity;
import com.memoseed.mozicaplayer.activities.PlayerActivity;

import org.json.JSONObject;

/**
 * Created by MohamedSayed on 9/2/2017.
 */

public class GetCover extends AsyncTask<String, String, JSONObject > {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(args[0]);

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
           // MainActivity.getInstance().libraryPagerAdapter.getItem()
            PlayerActivity.getInstance().playerFragment.updateCoverArt();
        }

}
