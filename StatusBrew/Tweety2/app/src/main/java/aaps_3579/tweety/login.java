package aaps_3579.tweety;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class login extends AppCompatActivity {

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;
    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    ProgressDialog progressDialog;
    RequestToken requestToken;
    FrameLayout frameLayout;
    private Button button;
    private boolean verify = false;
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);
        final SharedPreferences sharedPreferences = getSharedPreferences("token", MODE_PRIVATE);
        final String OAuthKey = sharedPreferences.getString("token_key", null);
        final String OAuthSecret = sharedPreferences.getString("token_secret", null);
        System.out.println(OAuthKey + "--" + OAuthSecret + "---In Login");
        if (OAuthKey != null && OAuthSecret != null) {

            progressDialog = new ProgressDialog(login.this);
            progressDialog.setMessage("Signing IN...");
            progressDialog.setCancelable(false);
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey(Helper.TWITTER_CONSUMER_KEY)
                    .setOAuthConsumerSecret(Helper.TWITTER_CONSUMER_SECRET)
                    .setOAuthAccessToken(OAuthKey)
                    .setOAuthAccessTokenSecret(OAuthSecret);
            TwitterFactory tf = new TwitterFactory(cb.build());

            final Twitter twitter = tf.getInstance();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.show();
                            }
                        });
                        twitter.verifyCredentials();
                        Helper.accessToken = new AccessToken(OAuthKey, OAuthSecret);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                            }
                        });
                        startActivity(new Intent(login.this, Main.class));

                    } catch (TwitterException e) {

                        e.printStackTrace();
                    }
                }
            }).start();


        }

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        makeFolder();
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        button = (Button) findViewById(R.id.dummy_button);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NetworkCheck networkCheck = new NetworkCheck(login.this);
                boolean networkAvailable = networkCheck.isNetworkAvailable();
                if (networkAvailable) {
                    ConfigurationBuilder cb = new ConfigurationBuilder();
                    cb.setDebugEnabled(true)
                            .setOAuthConsumerKey(Helper.TWITTER_CONSUMER_KEY)
                            .setOAuthConsumerSecret(Helper.TWITTER_CONSUMER_SECRET);
                    TwitterFactory tf = new TwitterFactory(cb.build());
                    final Twitter twitter = tf.getInstance();


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog = new ProgressDialog(login.this);
                                        progressDialog.setMessage("Starting Browser");
                                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                        progressDialog.setCancelable(false);
                                        progressDialog.show();
                                    }
                                });

                                requestToken = twitter.getOAuthRequestToken();
                                String authorizationURL = requestToken.getAuthorizationURL();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();
                                    }
                                });
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authorizationURL));

                                startActivityForResult(intent, 0);

                            } catch (Exception e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();
                                    }
                                });
                                Snackbar.make(frameLayout, "Please Verify Your APP", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }).start();


                } else {
                    Snackbar.make(frameLayout, "Network Unavailable", Snackbar.LENGTH_SHORT).show();
                }


            }
        });
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("In On Activity result" + requestCode + "----" + resultCode);
        if (requestCode == 0) {
            //   System.out.println("hi");
            Intent intent = new Intent(this, pin.class);
            intent.putExtra("request_token", requestToken);
            startActivity(intent);
            finish();

        }
    }

    void makeFolder() {
        if (ActivityCompat.checkSelfPermission(login.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(login.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            File f = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "Tweety");
            if (!f.exists())
                f.mkdirs();
        } else {
            ActivityCompat.requestPermissions(login.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 99);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 99) {
            if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                File f = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "Tweety");
                if (!f.exists())
                    f.mkdirs();
            } else {
                Snackbar.make(frameLayout, "Permission Denied", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
