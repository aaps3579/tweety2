package aaps_3579.tweety;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class home extends AppCompatActivity {

    LinearLayout cover_pic;
    TextView tweets, followers, following, name;
    ImageView profile_pic;

    URL url;
    HttpURLConnection httpURLConnection;
    InputStream inputStream;
    Bitmap bitmap;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        progressDialog = new ProgressDialog(home.this);
        progressDialog.setMessage("Fetching Details");
        progressDialog.setCancelable(false);
        profile_pic = (ImageView) findViewById(R.id.profile_pic);
        cover_pic = (LinearLayout) findViewById(R.id.cover_pic);
        tweets = (TextView) findViewById(R.id.textView6);
        followers = (TextView) findViewById(R.id.textView5);
        following = (TextView) findViewById(R.id.textView7);
        name = (TextView) findViewById(R.id.textView8);
        System.out.println("in home");
        try {
            update_home();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    public void update_home() throws TwitterException {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        System.out.println(Helper.accessToken.getToken() + "------" + Helper.accessToken.getTokenSecret());
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(Helper.TWITTER_CONSUMER_KEY)
                .setOAuthConsumerSecret(Helper.TWITTER_CONSUMER_SECRET)
                .setOAuthAccessToken(Helper.accessToken.getToken())
                .setOAuthAccessTokenSecret(Helper.accessToken.getTokenSecret());
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        showProgress();
        final User user = twitter.verifyCredentials();
        name.setText(user.getName());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String originalProfileImageURL = user.getOriginalProfileImageURL();
                    url = new URL(originalProfileImageURL);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.connect();
                    inputStream = httpURLConnection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            profile_pic.setImageBitmap(bitmap);
                        }
                    });


                    String profileBannerMobileURL = user.getProfileBannerMobileURL();
                    url = new URL(profileBannerMobileURL);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.connect();
                    inputStream = httpURLConnection.getInputStream();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cover_pic.setBackgroundDrawable(new BitmapDrawable(inputStream));
                            followers.setText(user.getFollowersCount() + "");
                            following.setText(user.getFriendsCount() + "");
                            tweets.setText(user.getStatusesCount() + "");
                        }
                    });

                } catch (Exception ex) {
                    stopProgress();
                    ex.printStackTrace();
                    Snackbar.make(getCurrentFocus(), "Error Occured Try Again", Snackbar.LENGTH_SHORT).show();
                    finish();
                }

                stopProgress();
            }
        }).start();


    }

    void showProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                progressDialog.show();
            }
        });
    }

    void stopProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });
    }


}
