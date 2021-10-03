package com.luoboanalytics.study_chinese_with_twitter;

import android.os.StrictMode;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


// Unused attempt to make a generic http request function
public class getRequest{

    private static final String consumerKey = BuildConfig.CONSUMER_KEY;
    private static final String consumerSecret = BuildConfig.CONSUMER_SECRET;
    private static final String bearerToken = BuildConfig.BEARER_TOKEN;

    public void getString(String url){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {

            final OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer " + bearerToken)
                    .build();


            client.newCall(request).enqueue(new Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                        String body = responseBody.string();
                        ArrayList<Tweet> tweetsList = null;

                        try{
                            JSONObject jsonObject = new JSONObject(body);



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }
            });
        }
        catch (Exception e)
        {
            Log.e("ERROR", e.toString());
        }

    };
}
