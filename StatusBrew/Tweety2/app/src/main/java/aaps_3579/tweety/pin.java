package aaps_3579.tweety;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class pin extends AppCompatActivity {

    EditText PIN;
    Button verify;
    RequestToken requestToken = null;
    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_pin);
        progressDialog = new ProgressDialog(pin.this);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setCancelable(false);
        sharedPreferences = getSharedPreferences("token", MODE_PRIVATE);
        Intent i = getIntent();
        requestToken = (RequestToken) i.getExtras().get("request_token");
        PIN = (EditText) findViewById(R.id.PIN);
        verify = (Button) findViewById(R.id.verify);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkCheck networkCheck = new NetworkCheck(pin.this);
                boolean networkAvailable = networkCheck.isNetworkAvailable();
                if (networkAvailable) {
                    if (PIN.getText().length() > 0) {
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
                                    showProgress();
                                    Helper.accessToken = twitter.getOAuthAccessToken(requestToken, PIN.getText().toString());
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("token_key", Helper.accessToken.getToken());
                                    editor.putString("token_secret", Helper.accessToken.getTokenSecret());
                                    editor.commit();
                                    stopProgress();

                                    startActivity(new Intent(pin.this, Main.class));
                                    finish();
                                } catch (Exception ex) {
                                    stopProgress();

                                    Snackbar.make(getCurrentFocus(), "Unable to Fetch Access Token", Snackbar.LENGTH_SHORT).show();

                                    ex.printStackTrace();
                                }
                            }
                        }).start();

                    } else {
                        Snackbar.make(v, "Please Enter Correct PIN", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(v, "Network Unavailable", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    void saveAccessToken() {

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
