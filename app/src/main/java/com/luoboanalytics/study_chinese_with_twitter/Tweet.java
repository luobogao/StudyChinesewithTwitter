package com.luoboanalytics.study_chinese_with_twitter;

import com.luoboanalytics.study_chinese_with_twitter.parse.Dictionary;

import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;

public class Tweet implements Serializable {
    private String text;
    private String user;
    private String pic;  // URL of the user's profile pic


    public Tweet(String muser, String mtext, String mProfilePicURL) throws JSONException {
        text = mtext;
        user = muser;
        pic  = mProfilePicURL;

    }

    public String getText(){
        return text;
    }
    public String getUser(){
        return user;
    }
    public String getPic(){
        return pic;
    }



}
