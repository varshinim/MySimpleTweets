package com.codepath.apps.restclienttemplate.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcel;
import org.parceler.Parcels;

public class TweetActivity extends AppCompatActivity {

    public ImageView ivProfileImage;
    public TextView tvUserName;
    public TextView tvBody;
    public Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        tweet = Parcels.unwrap(intent.getParcelableExtra("Tweet"));

        Log.d("Tweet Recieved:", tweet.getBody());

        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        Glide.with(getApplicationContext()).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvUserName.setText(tweet.getUser().getName());
        tvBody = (TextView) findViewById(R.id.tvBody);
        tvBody.setText(tweet.getBody());

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

}
