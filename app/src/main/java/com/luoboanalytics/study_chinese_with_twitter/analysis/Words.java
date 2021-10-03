package com.luoboanalytics.study_chinese_with_twitter.analysis;

import android.util.Log;

import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class Words implements Serializable {
    private static ArrayList<Word> WordList;

    public Words(ArrayList<Word> newWords){
        WordList = newWords;
    }

    public Integer getMaxFrequency(){
        Integer max = 0;
        ArrayList<Integer> freqList = new ArrayList();
        freqList.add(0);

        for (Word word : WordList){

            try {
                Integer frequency = word.getFrequency();
                freqList.add(frequency);
            }
            catch (Exception e){
                // Word is not in dictionary, skip

            }

        }

        max = Collections.max(freqList);

        return max;
    }
    public Integer getMaxHSK(){
        Integer max = 0;
        ArrayList<Integer> hskList = new ArrayList();
        hskList.add(0);

        for (Word word : WordList){

            try {
                Integer frequency = word.getHSK();
                hskList.add(frequency);
            }
            catch (Exception e){
                // Word is not in dictionary, skip

            }

        }

        max = Collections.max(hskList);

        return max;
    }
    public Integer getSecondMaxFrequency() {
        int highest = 0;
        int secondHighest = 0;

        for (Word word : WordList) {

            try {
                Integer frequency = word.getFrequency();
                if (frequency > highest) {
                    secondHighest = highest;
                    highest = frequency;
                } else if (frequency > secondHighest) {
                    secondHighest = frequency;
                }
            } catch (Exception e) {
                // Word is not in dictionary, skip

            }

        }
        return secondHighest;
    }
    public Integer getSecondMaxHSK() {
        int highest = 0;
        int secondHighest = 0;

        for (Word word : WordList) {

            try {
                Integer hsk = word.getHSK();
                if (hsk > highest) {
                    secondHighest = highest;
                    highest = hsk;
                } else if (hsk > secondHighest) {
                    secondHighest = hsk;
                }
            } catch (Exception e) {
                // Word is not in dictionary, skip

            }

        }
        return secondHighest;
    }

    public ArrayList getWordsHSK(Integer hskLimit){
        ArrayList wordsOut = new ArrayList<Word>();

        for (Word word : WordList){

            try {
                Integer hsk = word.getHSK();
                if (word.getHSK() >= hskLimit){
                    //Log.d("WORDS", "Adding word: " + word.getDisplayText() + " freq: " + Integer.toString(hsk));
                    wordsOut.add(word);
                }
                else
                {
                    // Skip adding word, frequency is too low
                    //Log.d("WORDS", "Skipping word: " + word.getDisplayText() + " freq: " + Integer.toString(frequency));
                }
            }
            catch (Exception e){
                // Word is not in dictionary, skip

            }

        }
        return wordsOut;
    }
    public ArrayList getWordsFreq(Integer frequencyLimit){
        ArrayList wordsOut = new ArrayList<Word>();

        for (Word word : WordList){

            try {
                Integer frequency = word.getFrequency();
                if (word.getFrequency() < frequencyLimit){
                    //Log.d("WORDS", "Adding word: " + word.getDisplayText() + " freq: " + Integer.toString(frequency));
                    wordsOut.add(word);
                }
                else
                {
                    // Skip adding word, frequency is too low
                    //Log.d("WORDS", "Skipping word: " + word.getDisplayText() + " freq: " + Integer.toString(frequency));
                }
            }
            catch (Exception e){
                // Word is not in dictionary, skip

            }

        }
        return wordsOut;
    }

}
