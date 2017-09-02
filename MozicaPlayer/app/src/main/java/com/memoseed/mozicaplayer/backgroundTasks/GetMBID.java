package com.memoseed.mozicaplayer.backgroundTasks;

import android.os.AsyncTask;

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

            new GetCover().execute(mbid);
        }

}
