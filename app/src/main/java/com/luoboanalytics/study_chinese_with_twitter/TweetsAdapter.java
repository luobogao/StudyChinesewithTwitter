package com.luoboanalytics.study_chinese_with_twitter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.luoboanalytics.study_chinese_with_twitter.analysis.Word;
import com.luoboanalytics.study_chinese_with_twitter.parse.Dictionary;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {


    private List<Tweet> mTweets;

    public TweetsAdapter(List<Tweet> tweets){
        mTweets = tweets;
    }

    private ClickTweetCallback clickTweetCallback;
    public void registerCallback(ClickTweetCallback callback){
        clickTweetCallback = callback;
    }


    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public ImageView profileImage;
        public TextView userText;
        private TextView wordText; // Text showing the one or two words that are a higher vocab level


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.tweet_text);
            profileImage = (ImageView) itemView.findViewById(R.id.profile_image);
            userText = (TextView) itemView.findViewById(R.id.userText);
            wordText = (TextView) itemView.findViewById(R.id.cardWordTextView);

            // Store the context
            // Attach a click listener to the entire row view
            itemView.setOnClickListener(this);

        }

        // Handles the row being being clicked
        @Override
        public void onClick(View view) {
            Log.i("USER", "CLICKED");
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                Tweet tweet = mTweets.get(position);
                String tweetText = tweet.getText();
                Log.i("USER", tweetText);
                clickTweetCallback.clickedTweet(tweet);
            }
        }



    }

    // Called when adapter is created, initializes viewholders.
    @Override
    public TweetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    // How data is passed to ViewHolder
    @Override
    public void onBindViewHolder(TweetsAdapter.ViewHolder holder, int position){
        // Get the data model based on position
        Tweet tweet = mTweets.get(position);

        // Avatar
        ImageView imageView = holder.profileImage;
        //imageView.setImageResource();
        Bitmap bm = getImageBitmap(tweet.getPic());
        imageView.setImageBitmap(bm);

        // User
        TextView userView = holder.userText;
        userView.setText(tweet.getUser());

        // Tweet Text
        TextView textView = holder.nameTextView;
        textView.setText(tweet.getText());

        // Word Text
        TextView wordView = holder.wordText;
        ArrayList parsed = null;
        try {
            parsed = Dictionary.Parse(tweet.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Word secondLast = (Word) parsed.get(parsed.size() - 1); // TEST - for now just choose second-to-last word to display
        try {
            wordView.setText(secondLast.getSimplified());
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    // Return total count of items in list
    @Override
    public int getItemCount(){
        return mTweets.size();
    }

    private Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e(TAG, "Error getting bitmap", e);
        }
        return bm;
    }

}
