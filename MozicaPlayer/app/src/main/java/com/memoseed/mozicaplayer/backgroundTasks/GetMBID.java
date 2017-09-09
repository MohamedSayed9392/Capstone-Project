package com.memoseed.mozicaplayer.backgroundTasks;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by MohamedSayed on 9/2/2017.
 */

public class GetMBID extends AsyncTask<String, String, JSONObject > {

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
            String id = "";
            try {
                JSONArray recordings = json.getJSONArray("recordings");
                if(recordings.length()>0) {
                    JSONArray releases = recordings.getJSONObject(0).getJSONArray("releases");
                    if(releases.length()>0) {
                        id = releases.getJSONObject(0).getString("id");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            new GetCover().execute(id);
        }

}
