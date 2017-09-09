package com.memoseed.mozicaplayer.backgroundTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.memoseed.mozicaplayer.activities.MainActivity;
import com.memoseed.mozicaplayer.activities.PlayerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by MohamedSayed on 9/2/2017.
 */

public class GetCover extends AsyncTask<String, String, JSONObject > {

    String TAG = getClass().getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl("http://coverartarchive.org/release/"+args[0]);

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            String image = "";
            try {
                Log.d(TAG,"json - "+json.toString());
                JSONArray images = json.getJSONArray("images");
                Log.d(TAG,"images length - "+images.length());
                if(images.length()>0) {
                    Log.d(TAG,"images.getJSONObject(0) - "+images.getJSONObject(0).toString());
                    image = images.getJSONObject(0).getString("image");
                    Log.d(TAG,"image - "+image);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG,"image - "+image);
            if(PlayerActivity.getInstance()!=null) PlayerActivity.getInstance().playerFragment.updateCoverArt(image);
        }

}
