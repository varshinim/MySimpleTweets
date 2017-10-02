package com.codepath.apps.restclienttemplate.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import static android.R.attr.name;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder>{

    // create VIewHolder class (findVIewById lookups)
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView ivProfileImage;
        public TextView tvUserName;
        public TextView tvBody;
        public TextView tvTimeStamp;

        public ViewHolder(View tweet){
            super(tweet);
            // perform findViewById lookups
            ivProfileImage = (ImageView) tweet.findViewById(R.id.ivProfileImage);
            tvUserName = (TextView) tweet.findViewById(R.id.tvUserName);
            tvBody = (TextView) tweet.findViewById(R.id.tvBody);
            tvTimeStamp = (TextView) tweet.findViewById(R.id.tvTimeStamp);
            tweet.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d("On Tweet Click:", "Clicked");
            itemListener.recyclerViewListClicked(v, this.getLayoutPosition());

        }
    }

    public interface RecyclerViewClickListener {
        public void recyclerViewListClicked(View v, int position);
    }

    private List<Tweet> tweets;
    private Context context;
    private static RecyclerViewClickListener itemListener;

    public TweetAdapter(Context context, List<Tweet> tweets, RecyclerViewClickListener itemListener){
        this.tweets = tweets;
        this.context = context;
        this.itemListener = itemListener;
    }

    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    // for each row inflate the layout and cache references to ViewHolder
    @Override
    public TweetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView;
        TweetAdapter.ViewHolder viewHolder;
        tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        viewHolder = new TweetAdapter.ViewHolder(tweetView);

        return viewHolder;
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(TweetAdapter.ViewHolder holder, int position) {
        //get data according to position
        Tweet tweet = tweets.get(position);
        //populate the view according to the position
        holder.tvUserName.setText(tweet.getUser().getName());
        holder.tvBody.setText(tweet.getBody());
        holder.tvTimeStamp.setText(getRelativeTimeAgo(tweet.getCreatedAt()));
        Glide.with(context).load(tweet.getUser().getProfileImageUrl()).into(holder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(Date date) {
        String relativeDate = "";

        long dateMillis = date.getTime();
        relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                System.currentTimeMillis(), DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        // relativeDate = date.toString();

        return relativeDate;
    }
}