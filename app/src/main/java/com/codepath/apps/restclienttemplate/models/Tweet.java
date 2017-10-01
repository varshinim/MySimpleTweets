package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Comparator;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.SimpleTimeZone;

import static android.R.attr.format;
import static com.loopj.android.http.AsyncHttpClient.log;

public class Tweet {
    // list out the attributes
    private String body;
    private long uid;   //db id for tweet
    private User user;
    private String createdAt;

    private SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreatedAt() {
        Date date = null;
        try {
            date = parseDate(createdAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = formatDate(createdAt);
    }

    //deserialize the JSON
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException{
        Tweet tweet = new Tweet();
        //Extract the values from JSON
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));

        return tweet;
    }

    public String formatDate(Date date){
        sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
        String dateStr = sdf.format(date);
        return dateStr;
    }

    public Date parseDate(String dateStr) throws ParseException{
        sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
        sdf.setLenient(true);
        Date date = sdf.parse(dateStr);
        return date;
    }

    public static class orderByCreatedAt implements Comparator<Tweet> {
        @Override
        public int compare(Tweet t1, Tweet t2) {
            return t2.getCreatedAt().compareTo(t1.getCreatedAt());
        }
    }

}
