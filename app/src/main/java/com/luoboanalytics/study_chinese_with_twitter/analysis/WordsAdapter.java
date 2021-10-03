package com.luoboanalytics.study_chinese_with_twitter.analysis;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luoboanalytics.study_chinese_with_twitter.ClickTweetCallback;
import com.luoboanalytics.study_chinese_with_twitter.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class WordsAdapter extends RecyclerView.Adapter<WordsAdapter.ViewHolder>{
    private List<Word> words;

    public WordsAdapter(ArrayList<Word> mwords){
        Log.i("WordsAdapter", "Building WordsAdapter with " + Integer.toString(mwords.size()) + " words");
        words = mwords;
    };



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView WordText; // The chinese word text
        public TextView DefinitionText;
        public TextView PinyinText;
        private Context context;
        private TextView HSKText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            WordText = (TextView) itemView.findViewById(R.id.WordText);
            PinyinText = (TextView) itemView.findViewById(R.id.PinyinView);
            DefinitionText = (TextView) itemView.findViewById(R.id.DefinitionText);
            HSKText = (TextView) itemView.findViewById(R.id.HSKView);
            this.context = context;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

        }
    }
    @Override
    public WordsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View WordView = inflater.inflate(R.layout.word_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(WordView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WordsAdapter.ViewHolder holder, int position) {

        Word word = words.get(position);

        try{
            TextView wordView  = holder.WordText;
            String displayText = word.getDisplayText();
            wordView.setText(displayText);

        }
        catch (JSONException e){
            Log.e("Word Adapter", "Failed to get display text");
        }
        try{
            TextView pinyinView = holder.PinyinText;
            String    pinyin      = word.getPinyin();
            pinyinView.setText(pinyin);

        }
        catch (JSONException e){
            Log.e("Word Adapter", "Failed to get Pinyin");
        }
        try{
            String definition  = word.getDefinitionHTML();
            Log.i("WORD ADAPTER", "HTML: " + definition);
            TextView defView = holder.DefinitionText;
            defView.setText(Html.fromHtml(definition, Html.FROM_HTML_MODE_COMPACT));

        }
        catch (JSONException e){
            Log.e("Word Adapter", "Failed to get Definition");
        }
        try{
            Integer hsk = word.getHSK();
            TextView hskView = holder.HSKText;
            hskView.setText("HSK: " + Integer.toString(hsk));

        }
        catch (JSONException e){
            Log.e("Word Adapter", "Failed to get HSK");
        }



    }

    @Override
    public int getItemCount() {
        return words.size();
    }


}
