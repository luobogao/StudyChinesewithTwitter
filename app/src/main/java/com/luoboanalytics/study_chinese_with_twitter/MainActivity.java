package com.luoboanalytics.study_chinese_with_twitter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luoboanalytics.study_chinese_with_twitter.analysis.Analysis;
import com.luoboanalytics.study_chinese_with_twitter.db.DataBaseHelper;
import com.luoboanalytics.study_chinese_with_twitter.parse.Dictionary;

import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Serializable, TweetCallback, ClickTweetCallback{

    private TwitterRequests twitterRequests;

    ArrayList tweets = Tweets.getTweets();
    RecyclerView rvTweets;
    TweetsAdapter adapter;

    DataBaseHelper dbhelper;



    // Click to get tweets
    public void clickButton(View view)
    {
        twitterRequests.getReTweets();
    }

    // Callback when user selects tweet
    public void clickedTweet(Tweet tweet){
        Intent intent = new Intent(this, Analysis.class);
        intent.putExtra("TWEETOBJ", tweet);
        startActivity(intent);
    }

    // Callback after async tweet requests
    public void tweetCallback(Tweet newTweet){
        tweets.add(0, newTweet);
        adapter.notifyItemInserted(0);

        // Scroll to top after adding new items
        rvTweets.scrollToPosition(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();
        super.onCreate(savedInstanceState);

        twitterRequests = new TwitterRequests();
        twitterRequests.registerCallback(this);


        rvTweets = (RecyclerView) findViewById(R.id.rvTweets);

        adapter = new TweetsAdapter(tweets);
        adapter.registerCallback(this);

        rvTweets.setAdapter(adapter);
        rvTweets.setLayoutManager(new LinearLayoutManager(this));

        // Decorations
        SpacesItemDecoration spacing = new SpacesItemDecoration(10);
        rvTweets.addItemDecoration(spacing);

        // Test chinese parsing

        // Load Dictionary
        DataBaseHelper.openDatabases();

        Dictionary.BuildAll(this); // Trigger the loading of all dictionaries and trees from assets

        ArrayList parsed = null;
        ArrayList parsed2 = null;
        try {
            parsed = Dictionary.Parse("哦哦我youtube账号原来还能用啊");
            parsed2 = Dictionary.Parse("今天的X也是可愛又炫目");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("PARSE DONE", parsed.toString());
        Log.i("PARSE DONE", parsed2.toString());



    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }




}