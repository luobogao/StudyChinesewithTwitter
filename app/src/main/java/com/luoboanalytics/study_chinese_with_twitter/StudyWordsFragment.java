package com.luoboanalytics.study_chinese_with_twitter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.luoboanalytics.study_chinese_with_twitter.R;
import com.luoboanalytics.study_chinese_with_twitter.analysis.Word;
import com.luoboanalytics.study_chinese_with_twitter.analysis.Words;
import com.luoboanalytics.study_chinese_with_twitter.analysis.WordsAdapter;

import java.util.ArrayList;

public class StudyWordsFragment extends Fragment {

    RecyclerView rvWords;
    WordsAdapter adapter;
    // https://www.androidhive.info/2020/01/viewpager2-pager-transformations-intro-slider-pager-animations-pager-transformations/

    private Integer frequencyLimit;
    private Integer hsklimit;

    Words words;
    public StudyWordsFragment(Words newWords,  Integer HSK_Limit){
        words = newWords;
        Log.d("Words Fragment", "Building wordsfragment with frequency limit: " + Integer.toString(HSK_Limit));
        hsklimit = HSK_Limit;
    }
   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
       View wordView = inflater.inflate(R.layout.fragment_analysis_study_words, container, false);
       RecyclerView rvWords = (RecyclerView) wordView.findViewById(R.id.AnalysisWordRecycle);

       ArrayList<Word> filteredWords = words.getWordsHSK(hsklimit);

       //Log.i("Fragment", filteredWords.get(0).toString());
       adapter = new WordsAdapter(filteredWords);

       rvWords.setAdapter(adapter);
       rvWords.setLayoutManager(new LinearLayoutManager(getActivity()));


       return wordView;
   }
}