package com.luoboanalytics.study_chinese_with_twitter;



import java.util.ArrayList;

public class Tweets{

    public static ArrayList tweets = new ArrayList<Tweet>();

    public static ArrayList getTweets(){
        return tweets;
    }

    public static void addTweet(Tweet tweet){
        tweets.add(0, tweet);


    }

}
