package com.codepath.apps.restclienttemplate;

import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;

public class ComposeTweetDialogFragment extends AppCompatDialogFragment{

    View mView;
    ImageView profileImage;
    TextView name;
    TextView screenName;
    EditText etTweet;
    TextView tvCharCount;
    Button btnTweet;
    User user;

    public interface ComposeTweetDialogListener {
        void onFinishEditDialog(Tweet tweet);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());

        mView = layoutInflater.inflate(R.layout.dialog_tweet, null);

        profileImage = (ImageView) mView.findViewById(R.id.ivImage);
        Glide.with(getContext()).load(user.getProfileImageUrl()).into(profileImage);
        name = (TextView) mView.findViewById(R.id.tvName);
        name.setText(user.getName());
        screenName = (TextView) mView.findViewById(R.id.tvScreenName);
        screenName.setText(user.getScreenName());
        etTweet = (EditText) mView.findViewById(R.id.etTweet);
        etTweet.addTextChangedListener(mTextEditorWatcher);
        tvCharCount = (TextView) mView.findViewById(R.id.tvCharCount);
        btnTweet = (Button) mView.findViewById(R.id.btnTweet);


        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("onClick", "Button Clicked");
                ComposeTweetDialogListener listener = (ComposeTweetDialogListener) getActivity();

                Tweet tweet = new Tweet();
                tweet.setBody(etTweet.getText().toString());
                tweet.setUser(user);

                final Calendar calender = Calendar.getInstance();
                Date d = calender.getTime();
                tweet.setCreatedAt(d);
                listener.onFinishEditDialog(tweet);
                // Close the dialog and return back to the parent activity
                dismiss();
            }
        });
        return mView;
    }

    public void data(User user){

        this.user = user;
    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textview to the current length
            tvCharCount.setText(140 - s.toString().length()+"");
        }

        public void afterTextChanged(Editable s) {
        }
    };

}
