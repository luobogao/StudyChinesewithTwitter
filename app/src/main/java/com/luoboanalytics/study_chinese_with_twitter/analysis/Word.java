package com.luoboanalytics.study_chinese_with_twitter.analysis;

import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class Word implements Serializable {

    private JSONObject word = new JSONObject();

    // Constructor for word that is not found in dictionary
    public Word(JSONObject wordJSON){
        //Log.i("WORD", "word: " + wordJSON.toString());
       word = wordJSON;
    }

    public Word(String text) throws JSONException {
        word.put("parsed_text", text);
        word.put("hsk", 0);
    }




    public String getSimplified() throws JSONException {
        if (word.has("parsed_text")){
            return "";
        }
        else {
            JSONArray simplifiedList = word.getJSONArray("simplified");
            return simplifiedList.getString(0);
        }
    }
    public String getTraditional() throws JSONException {
        if (word.has("parsed_text")){
            return "";
        }
        else {
            return word.getString("traditional");

        }
    }

    public Integer wordLength() throws JSONException {
        if (word.has("parsed_text")){
            return word.getString("parsed_text").length();
        }
        else
        {
            return word.getString("traditional").length();
        }
    }

    public Integer getFrequency() throws JSONException{
        return word.getInt("frequency");
    }
    public Integer getHSK() throws JSONException{
        Integer hsk = word.getInt("hsk");
        return hsk;
    }
    public String getDisplayText() throws JSONException {
        String out = "";
        if (word.has("parsed_text"))
        {
            out = word.getString("parsed_text");
        }
        else{
            out = word.getJSONArray("simplified").getString(0);
        }

        return out;

    }
    public String getPinyin() throws JSONException {
        String pinyin = "";
        try{
            JSONArray pinyinArray = word.getJSONArray("pinyin");
            StringBuilder pinyinOut = new StringBuilder();
            for (int i=0; i<pinyinArray.length(); i++) {
                pinyinOut.append(" - " + pinyinArray.getString(i));
            }
            pinyin = pinyinOut.toString();
        }
        catch (JSONException e){

        }
        return pinyin;

    }
    public String getDefinitionHTML() throws JSONException {
        StringBuilder englishHTML = new StringBuilder();

        // Slice the english definition, take first three definitions, return newline delimited string
        try{
            JSONArray englishList = word.getJSONArray("english");
            englishHTML.append("<table>");
            for (int i=0; i<englishList.length(); i++) {

                String[] entry = englishList.getString(i).split("\\$");
                String pinyin = "";
                String english = "";
                if (entry.length > 1){
                    pinyin = entry[0];
                    english = entry[1];
                }
                else
                {
                    english = entry [0];
                }
                englishHTML.append("<tr><td style=\"color:darkgrey\">" + pinyin + "&nbsp;&nbsp;&nbsp;</td><td>" + english + "</td></tr>");

            }
            englishHTML.append("</table>");

        }
        catch (JSONException e){

        }
        return englishHTML.toString();

    }


}
