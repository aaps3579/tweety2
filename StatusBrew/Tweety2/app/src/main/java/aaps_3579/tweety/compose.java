package aaps_3579.tweety;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import twitter4j.GeoLocation;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class compose extends AppCompatActivity {

    ImageView compose_pic, compose_attach_img;
    TextView compose_name, location_indicator;
    EditText compose_text;
    FloatingActionButton compose_location, compose_attach, compose_send;
    GPSTracker gpsTracker;
    GeoLocation location;
    File attach_path;
    ProgressDialog progressDialog;
    boolean location_flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_compose);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Details");
        progressDialog.setCancelable(false);
        showProgress();
        location_indicator = (TextView) findViewById(R.id.location_indicator);
        compose_name = (TextView) findViewById(R.id.compose_name);
        compose_text = (EditText) findViewById(R.id.compose_tweet);
        compose_pic = (ImageView) findViewById(R.id.compose_pic);
        compose_location = (FloatingActionButton) findViewById(R.id.compose_location);
        compose_attach = (FloatingActionButton) findViewById(R.id.compose_attach);
        compose_send = (FloatingActionButton) findViewById(R.id.compose_send);
        compose_attach_img = (ImageView) findViewById(R.id.compose_attach_img);

        compose_attach_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeAlert();
            }
        });
        compose_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(compose.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(compose.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    gpsTracker = new GPSTracker(compose.this);

                    if (location_flag == false) {
                        if (gpsTracker.canGetLocation()) {
                            System.out.println("Flag false started");
                            location_indicator.setText("Location ON");
                            location_flag = true;
                            location = new GeoLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude());


                        } else {
                            gpsTracker.showSettingsAlert();
                        }
                    } else {
                        location_indicator.setText("Location OFF");
                        location_flag = false;
                        location = null;
                        gpsTracker.stopUsingGPS();
                    }

                } else {
                    ActivityCompat.requestPermissions(compose.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
                }
            }
        });
        compose_attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(compose.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(compose.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    makeAlert();
                } else {
                    ActivityCompat.requestPermissions(compose.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
        });
        compose_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = compose_text.getText().toString();
                if (text.isEmpty()) {
                    Snackbar.make(v, "Can't Tweet Empty", Snackbar.LENGTH_SHORT).show();
                } else if (text.length() > 140) {

                    Snackbar.make(v, "Can't Tweet More Than 140 characters", Snackbar.LENGTH_SHORT).show();

                } else {
                    NetworkCheck networkCheck = new NetworkCheck(compose.this);
                    boolean networkAvailable = networkCheck.isNetworkAvailable();
                    if (networkAvailable) {
                        ConfigurationBuilder cb = new ConfigurationBuilder();
                        cb.setOAuthConsumerKey(Helper.TWITTER_CONSUMER_KEY)
                                .setOAuthConsumerSecret(Helper.TWITTER_CONSUMER_SECRET)
                                .setOAuthAccessToken(Helper.accessToken.getToken())
                                .setOAuthAccessTokenSecret(Helper.accessToken.getTokenSecret());
                        TwitterFactory t = new TwitterFactory(cb.build());
                        final Twitter twitter = t.getInstance();
                        final StatusUpdate update = new StatusUpdate(text);
                        if (location != null) {
                            System.out.println("location set");
                            update.setLocation(location);
                        }
                        if (attach_path != null) {
                            update.setMedia(attach_path);
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    progressDialog.setMessage("Tweeting");
                                    showProgress();
                                    twitter.updateStatus(update);
                                    stopProgress();

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            compose_text.setText("");
                                            compose_attach_img.setImageResource(R.mipmap.ic_launcher);
                                            Snackbar.make(getCurrentFocus(), "Tweeted Sucessfully", Snackbar.LENGTH_SHORT).show();
                                        }
                                    });
                                } catch (TwitterException e) {
                                    e.printStackTrace();
                                    stopProgress();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            compose_text.setText("");

                                            compose_attach_img.setImageResource(R.mipmap.ic_launcher);
                                            Snackbar.make(getCurrentFocus(), "Not Able To Tweet", Snackbar.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }).start();

                    } else {
                        Snackbar.make(v, "No Network", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getData();
                } catch (Exception ex) {

                    ex.printStackTrace();
                }
            }
        }).start();

    }

    void getData() throws Exception {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        System.out.println(Helper.accessToken.getToken() + "------" + Helper.accessToken.getTokenSecret());
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(Helper.TWITTER_CONSUMER_KEY)
                .setOAuthConsumerSecret(Helper.TWITTER_CONSUMER_SECRET)
                .setOAuthAccessToken(Helper.accessToken.getToken())
                .setOAuthAccessTokenSecret(Helper.accessToken.getTokenSecret());
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        final User user = twitter.verifyCredentials();


        String originalProfileImageURL = user.getOriginalProfileImageURL();
        URL url = new URL(originalProfileImageURL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoInput(true);
        httpURLConnection.connect();
        InputStream inputStream = httpURLConnection.getInputStream();
        final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                compose_name.setText(user.getName());

                compose_pic.setImageBitmap(bitmap);
                stopProgress();
            }
        });

    }


    void makeAlert() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                compose.this);
        myAlertDialog.setTitle("Upload Picture Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent pictureActionIntent = null;

                        pictureActionIntent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(
                                pictureActionIntent,
                                200);

                    }
                });

        myAlertDialog.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        Intent intent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);


                        startActivityForResult(intent,
                                201);

                    }
                });
        myAlertDialog.show();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(getCurrentFocus(), "Permission Granted", Snackbar.LENGTH_SHORT).show();
            } else {

                Snackbar.make(getCurrentFocus(), "Permission Denied", Snackbar.LENGTH_SHORT).show();
            }
        } else if (requestCode == 1) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(getCurrentFocus(), "Permission Granted", Snackbar.LENGTH_SHORT).show();
            } else {

                Snackbar.make(getCurrentFocus(), "Permission Denied", Snackbar.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            //gallery
            if (resultCode == RESULT_OK) {
                Uri gallery_data = data.getData();
                File f = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "Twitter");
                if (!f.exists())
                    f.mkdirs();

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                    Date d = new Date();
                    String filename = sdf.format(d) + ".jpg";
                    InputStream is = (FileInputStream) getContentResolver().openInputStream(gallery_data);
                    Bitmap bmp = BitmapFactory.decodeStream(is);
                    FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + File.separator + "Twitter" + File.separator + filename);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                    attach_path = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "Twitter" + File.separator + filename);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                compose_attach_img.setImageResource(android.R.color.transparent);
                compose_attach_img.setImageURI(gallery_data);


            }
        } else if (requestCode == 201) {
            //camera

            File f = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "Twitter");
            if (!f.exists())
                f.mkdirs();

            Bitmap photo = (Bitmap) data.getExtras().get("data");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            Date d = new Date();
            String filename = sdf.format(d) + ".jpg";
            try {
                FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + File.separator + "Twitter" + File.separator + filename);
                photo.compress(Bitmap.CompressFormat.JPEG, 120, fos);
                attach_path = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "Twitter" + File.separator + filename);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            compose_attach_img.setImageResource(android.R.color.transparent);
            compose_attach_img.setImageBitmap(photo);

        }
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
