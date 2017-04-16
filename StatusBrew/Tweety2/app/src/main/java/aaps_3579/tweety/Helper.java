package aaps_3579.tweety;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * Created by HP_PC on 14-04-2017.
 */
public class Helper extends Application {

    public static String TWITTER_CONSUMER_KEY = "2KPuF2iHHEO2HmFDZ5S14pZP4";
    public static String TWITTER_CONSUMER_SECRET = "zdnxxAK9DhOFMf1w0NoHpnnCQaaeo2eVPxicOUeNeagGjSG6ZL";
    public static AccessToken accessToken = null;
    public static List<Tweet> tweetList;


}

