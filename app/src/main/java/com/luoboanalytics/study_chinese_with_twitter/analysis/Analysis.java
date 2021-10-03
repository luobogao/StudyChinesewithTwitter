package com.luoboanalytics.study_chinese_with_twitter.analysis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.luoboanalytics.study_chinese_with_twitter.R;
import com.luoboanalytics.study_chinese_with_twitter.Tweet;
import com.luoboanalytics.study_chinese_with_twitter.parse.Dictionary;
import com.luoboanalytics.study_chinese_with_twitter.translate.Translate;
import com.luoboanalytics.study_chinese_with_twitter.translate.translateCallback;


import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;

public class Analysis extends AppCompatActivity implements Serializable, translateCallback {

    TabLayout tabLayout;
    ViewPager2 viewPager;
    TextView translationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);
        Context context = getApplicationContext();

        Intent intent = getIntent();

        // Get the full Tweet obj passed through intent
        Tweet tweet = (Tweet) getIntent().getSerializableExtra("TWEETOBJ");


        // Get Tweet text
        String tweetText = tweet.getText();
        TextView tweetTextView = findViewById(R.id.AnalysisTweet);
        tweetTextView.setText(tweetText);

        // Translation
        translationView = findViewById(R.id.translationView);
        Translate translator = new Translate();
        translator.registerCallback(this);
        translator.translate(tweetText);

        ArrayList parsed = null;
        try {
            parsed = Dictionary.Parse(tweet.getText());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Analysis", "Failed to parse tweet: " + tweet.getText());
        }
        Words words = new Words(parsed);




        // Set up tabs
        viewPager = findViewById(R.id.AnalysisViewPager);
        tabLayout = findViewById(R.id.AnalysisTab);

        viewPager.setAdapter(new AnalysisAdapter(this, words));
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText("Tab " + (position + 1))).attach();




    }

    public void translated(String translation){
        translationView.setText(translation);
    }

    public void onClickTranslate(){
        Log.i("Analysis", "clicked");
    }
}