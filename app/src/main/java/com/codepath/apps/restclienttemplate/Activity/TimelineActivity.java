package com.codepath.apps.restclienttemplate.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.codepath.apps.restclienttemplate.Adapter.TweetAdapter;
import com.codepath.apps.restclienttemplate.InfiniteScrollListener;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.ComposeTweetDialogFragment;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;

import cz.msebera.android.httpclient.Header;

import static android.R.attr.offset;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.codepath.apps.restclienttemplate.R.id.swipeContainer;
import static com.loopj.android.http.AsyncHttpClient.log;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetDialogFragment.ComposeTweetDialogListener{

    // Store a member variable for the listener
    private InfiniteScrollListener scrollListener;
    private SwipeRefreshLayout swipeContainer;

    private TwitterClient client;
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    LinearLayoutManager linearLayoutManager;

    String screenName;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        client = TwitterApp.getRestClient();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupViews();
        populateTimeline(false);
        getUser();

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeline(true);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        scrollListener = new InfiniteScrollListener(linearLayoutManager) {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                Log.d("onLoadMore: ", "page = "+page);
                Log.d("onLoadMore: ", "totalItemsCount = "+totalItemsCount);
                loadNextDataFromApi(totalItemsCount);
                return true;
            }
        };
        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        populateTimeline(false);
        // tweetAdapter.notifyItemInserted(tweets.size()-1);
    }

    public void setupViews(){
        tweets = new ArrayList<>();  // instantiate the arratList( data source)
        rvTweets = (RecyclerView) findViewById(R.id.rvTweet);   // find the recyclerView
        //hook up listener for grid click
        TweetAdapter.RecyclerViewClickListener listener = new TweetAdapter.RecyclerViewClickListener(){
            @Override
            public void recyclerViewListClicked(View v, int position) {
                // create an intent to display the article
                Log.d("onClicked: ", ""+ position);
                Intent i = new Intent(getApplicationContext(), TweetActivity.class);
                // get the article to display
                Tweet tweet = tweets.get(position);
                // pass that article into intent
                i.putExtra("Tweet", Parcels.wrap(tweet));
                // launch the activity
                startActivity(i);
            }
        };

        tweetAdapter = new TweetAdapter(this, tweets, listener);  // construct the adapter form this datasource
        linearLayoutManager = new LinearLayoutManager(this);  // set layout manager
        rvTweets.setLayoutManager(linearLayoutManager);  // attach layout manager to RecyclerView
        rvTweets.setAdapter(tweetAdapter);   // Attach the adapter to the recyclerview to populate items
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.compose) {
            FragmentManager manager = getSupportFragmentManager();
            ComposeTweetDialogFragment dialog = new ComposeTweetDialogFragment();
            dialog.data(user);
            dialog.show(manager, "Filtered");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFinishEditDialog(Tweet tweet) {
        // post the tweet to Twitter APIs
        postTweet(tweet.getBody());
    }

    public void postTweet(String tweet){
        client.postTweet(tweet, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("Tweet response object: ", response.toString());
                Tweet tweet = null;
                try {
                    tweet = Tweet.fromJSON(response);
                    tweets.add(tweet);
                    Collections.sort(tweets, new Tweet.orderByCreatedAt());
                    tweetAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
            }
        });
    }

    public void populateTimeline(final boolean isRefresh){

        long maxId = tweets.isEmpty() ? -1:tweets.get(tweets.size()-1).getUid();
        long sinceId = isRefresh? tweets.get(0).getUid(): -1;

        if (isRefresh){
            Log.d("Refresh","swiped");
        }
        client.getHomeTimeline(maxId, sinceId, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Log.d("TwitterClient", response.toString());
                //super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                for(int i=0;i<response.length();i++){
                    try{
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        tweets.add(tweet);
                        tweetAdapter.notifyItemInserted(tweets.size()-1);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }

                if (isRefresh) {
                    swipeContainer.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
                //super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
                //super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
                //super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    public void getUser(){
        client.getAppUserSettings(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("AppUser", response.toString());
                try {
                    screenName = response.getString("screen_name");
                    Log.d("screen name", screenName);
                    getUserInformation();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("AppUser", responseString);
                throwable.printStackTrace();
                //super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("AppUser", errorResponse.toString());
                throwable.printStackTrace();
                //super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });
    }

    public void getUserInformation(){
        client.getUserInfo(screenName, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("UserInfo", response.toString());
                try {
                    user = User.fromJSON(response.getJSONObject(0));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("UserInfo", responseString);
                throwable.printStackTrace();
                //super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("UserInfo", errorResponse.toString());
                throwable.printStackTrace();
                //super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });
    }

}
