package aaps_3579.tweety;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;


public class Main extends AppCompatActivity {

    RecyclerView recyclerView;

    tweet_adapter tweet_adapter;
    Twitter twitter;
    ProgressDialog progressDialog;
    Thread t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        System.out.println("in main");
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("@Humblebrag");
        progressDialog = new ProgressDialog(Main.this);
        progressDialog.setMessage("Loading Tweets");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        Helper.tweetList = new ArrayList<>();
        tweet_adapter = new tweet_adapter(Helper.tweetList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(tweet_adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                NetworkCheck networkCheck = new NetworkCheck(Main.this);
                boolean networkAvailable = networkCheck.isNetworkAvailable();
                if (networkAvailable) {
                    System.out.println("CLicked" + position);
                    Intent i = new Intent(Main.this, view_pager.class);
                    i.putExtra("position", position);
                    startActivity(i);
                } else {
                    Snackbar.make(view, "Network Unavailable", Snackbar.LENGTH_SHORT).show();
                }
            }
        }));
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                get_Humblebrag();
            }
        });
        t.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NetworkCheck networkCheck;
        switch (item.getItemId()) {


            case R.id.home: {
                networkCheck = new NetworkCheck(Main.this);
                boolean networkAvailable = networkCheck.isNetworkAvailable();
                if (networkAvailable) {
                    startActivity(new Intent(Main.this, home.class));
                    System.out.println("started  home");
                    return true;
                } else {
                    Snackbar.make(getCurrentFocus(), "Network Unavailable", Snackbar.LENGTH_SHORT).show();
                }

            }
            case R.id.compose: {
                networkCheck = new NetworkCheck(Main.this);
                boolean networkAvailable = networkCheck.isNetworkAvailable();

                if (networkAvailable) {
                    startActivity(new Intent(Main.this, compose.class));
                    System.out.println("started  home");
                    return true;
                } else {
                    Snackbar.make(getCurrentFocus(), "Network Unavailable", Snackbar.LENGTH_SHORT).show();
                }
            }
            case R.id.logout: {
                SharedPreferences sharedPreferences = getSharedPreferences("token", MODE_PRIVATE);
                sharedPreferences.edit().clear()
                        .commit();
                if (t.isAlive())
                    t.interrupt();
                finish();
                // startActivity(new Intent(Main.this,login.class));
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void get_Humblebrag() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.show();
            }
        });

        ConfigurationBuilder cb = new ConfigurationBuilder();
        System.out.println(Helper.accessToken.getToken() + "------" + Helper.accessToken.getTokenSecret());
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(Helper.TWITTER_CONSUMER_KEY)
                .setOAuthConsumerSecret(Helper.TWITTER_CONSUMER_SECRET)
                .setOAuthAccessToken(Helper.accessToken.getToken())
                .setOAuthAccessTokenSecret(Helper.accessToken.getTokenSecret());
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        Paging paging = new Paging(1, 109);
        int count = 0;
        try {

            List<Status> statuses = twitter.getUserTimeline("Humblebrag", paging);
            for (Status status : statuses) {
                //String miniProfileImageURL = status.getUser().getBiggerProfileImageURL();
                if (status.getRetweetedStatus() == null) {
                    continue;
                }
                count++;
                User user = status.getRetweetedStatus().getUser();
                System.out.println("Got User");
                String originalProfileImageURL = user.getProfileImageURL();
                System.out.println(originalProfileImageURL);
                URL url = new URL(originalProfileImageURL);
                URLConnection connection = url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                Bitmap myBitmap1 = null;
                if (user.getProfileBannerURL() != null) {
                    String profileBannerURL = user.getProfileBannerURL();

                    {

                        URL url1 = new URL(user.getProfileBannerURL());

                        System.out.println(user.getProfileBannerURL());
                        URLConnection connection1 = url1.openConnection();
                        connection1.setDoInput(true);
                        connection1.connect();
                        try {
                            InputStream input1 = connection1.getInputStream();
                            myBitmap1 = BitmapFactory.decodeStream(input1);
                        } catch (FileNotFoundException ex) {
                            myBitmap1 = null;
                        }

                        System.out.println("Background set");
                    }
                }


                Tweet t = new Tweet(user.getName(), user.getScreenName(), user.getDescription(), user.getLocation(), Integer.toString(user.getFollowersCount()), Integer.toString(user.getFavouritesCount()), Integer.toString(user.getStatusesCount()), myBitmap, myBitmap1, status.getText().substring(status.getText().indexOf(":") + 1));

                Helper.tweetList.add(t);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tweet_adapter.notifyDataSetChanged();
                    }
                });
                if (count > 35) {
                    if (progressDialog.isShowing()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
                System.out.println("Count---" + count);


            }

        } catch (Exception e) {
            if (progressDialog.isShowing()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
            }
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }
}
