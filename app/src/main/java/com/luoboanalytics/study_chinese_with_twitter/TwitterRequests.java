package com.luoboanalytics.study_chinese_with_twitter;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import okhttp3.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.Request;

public class TwitterRequests extends MainActivity{

    private TweetCallback callbackNotify;

    public void registerCallback(TweetCallback callback){
        callbackNotify = callback;
    }


    private static final String consumerKey = BuildConfig.CONSUMER_KEY;
    private static final String consumerSecret = BuildConfig.CONSUMER_SECRET;
    private static final String bearerToken = BuildConfig.BEARER_TOKEN;

    private String nextToken = null;



    public void getReTweets() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Log.i("TEST", "GETTING RE-TWEETS");


        try {

            String userName = "luobogao5";
            String nexttoken = "";
            if (nextToken != null){
                nexttoken = "&next_token=" + nextToken;
            }
            String tweetsURL = "https://api.twitter.com/2/tweets/search/recent?query=from:" + userName + nexttoken + "&expansions=author_id,referenced_tweets.id,referenced_tweets.id.author_id";
            final OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(tweetsURL)
                    .header("Authorization", "Bearer " + bearerToken)
                    .build();


            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (!response.isSuccessful())
                            throw new IOException("Unexpected code " + response);
                        String body = responseBody.string();
                        ArrayList<Tweet> tweetsList = null;

                        try {
                            JSONObject jsonObject = new JSONObject(body);

                            // "next_token" is provided by the twitter api for pagination
                            nextToken = jsonObject.getJSONObject("meta").getString("next_token");
                            JSONArray tweets = jsonObject.getJSONObject("includes").getJSONArray("tweets");
                            JSONArray users = jsonObject.getJSONObject("includes").getJSONArray("users");
                            Log.i("Number of tweets:", Integer.toString(tweets.length()));

                            for (int i = 0; i < tweets.length(); i++) {
                                JSONObject tweetObj = tweets.getJSONObject(i);

                                Log.i("TWEET", tweetObj.toString());

                                /// GET TWEET FIELDS
                                String tweetText = tweetObj.getString("text");
                                String userID =  tweetObj.getString("author_id");

                                // QUERY USER FOR EACH TWEET
                                try {

                                    String url = "https://api.twitter.com/1.1/users/show.json?user_id=" + userID;

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

                                                try{
                                                    JSONObject userObj = new JSONObject(body);

                                                    // USER FIELDS
                                                    String screenName = userObj.getString("screen_name");
                                                    String profilePicURL = userObj.getString("profile_image_url_https");


                                                    Tweet newTweet = new Tweet(screenName, tweetText, profilePicURL);

                                                    // CALLBACK - new tweet has been added, update recyclerview
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            callbackNotify.tweetCallback(newTweet);


                                                        }
                                                    });



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

                            }




                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }
            });
        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        }
    }
    public void getTweets(){


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Log.i("TEST", "GETTING TWEETS");


        try {

            // Query specific ids
            String ids = "1374286982994616328,1374286982994616328,1373985073058447363";
            String tweetsURL = "https://api.twitter.com/2/tweets?ids=" + ids + "&tweet.fields=created_at&expansions=author_id&user.fields=created_at";

            final OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(tweetsURL)
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
                            JSONArray tweets = jsonObject.getJSONArray("data");
                            Log.i("Number of tweets:", Integer.toString(tweets.length()));



                            for (int i = 0; i<tweets.length(); i++){
                                JSONObject tweetObj = tweets.getJSONObject(i);
                                String tweet = tweetObj.getString("text");
                                String user = "test user";

                                //Tweets.addTweet(new Tweet(user, tweet));



                            }

                            // Callback - somehow this automatically runs, calling the callback on main ui thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //callbackNotify.tweetCallback();

                                }
                            });


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

    public static String getBearerToken(String key, String secret){


        String credString = consumerKey + ":" + consumerSecret;

        Log.i("TEST", "STARTING REQUEST");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            URL url = new URL("https://api.twitter.com/oauth2/token?grant_type=client_credentials");


            String encoding = Base64.encodeToString(credString.getBytes(), Base64.NO_WRAP);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty  ("Authorization", "Basic " + encoding);
            InputStream content = (InputStream)connection.getInputStream();
            BufferedReader in   =
                    new BufferedReader (new InputStreamReader(content));
            String line;
            while ((line = in.readLine()) != null) {
                JSONObject jsonObject = new JSONObject(line);
                String token = jsonObject.getString("access_token");
                if (token != null){
                    System.out.println("TOKEN:" + token);
                    return token;
                }
                else return null;

            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    return "abc";
    }

}
