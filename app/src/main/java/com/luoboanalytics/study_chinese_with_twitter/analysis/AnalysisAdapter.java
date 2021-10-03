package com.luoboanalytics.study_chinese_with_twitter.analysis;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.luoboanalytics.study_chinese_with_twitter.StudyWordsFragment;

import java.util.ArrayList;


public class AnalysisAdapter extends FragmentStateAdapter {
    Context context;
    int totalTabs = 3;
    Words words;
    public AnalysisAdapter(@NonNull FragmentActivity fragment, Words newWords){
        super(fragment);
        words = newWords;

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        //Fragment fragment = new StudyWordsFragment(words, 10000000);
        //return fragment;

        switch (position){
            case 0:
                Log.i("Adapter", "Select 0 fragment");
                return new StudyWordsFragment(words, 4);
            case 1:
                Log.i("Adapter", "Select 1 fragment");
                return new StudyWordsFragment(words, 0);
            default:
                return new StudyWordsFragment(words, 7);

        }
    }

    @Override
    public int getItemCount() {
        return totalTabs;
    }
}
