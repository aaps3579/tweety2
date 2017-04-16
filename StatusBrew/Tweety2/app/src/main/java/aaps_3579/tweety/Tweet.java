package aaps_3579.tweety;

import android.graphics.Bitmap;
import android.location.Location;

/**
 * Created by HP_PC on 15-04-2017.
 */
public class Tweet {

    public String username;
    public String handle;
    public String bio;
    public String location;
    public String followers;
    public String following;
    public String tweets;
    public Bitmap profile_pic;
    public Bitmap cover_pic;
    public String tweet_text;

    public Tweet(String username, String handle, String bio, String location, String followers, String following, String tweets, Bitmap profile_pic, Bitmap cover_pic, String tweet_text) {
        this.username = username;
        this.handle = handle;
        this.bio = bio;
        this.location = location;
        this.followers = followers;
        this.following = following;
        this.tweets = tweets;
        this.profile_pic = profile_pic;
        this.cover_pic = cover_pic;
        this.tweet_text = tweet_text;
    }

    public String getTweet_text() {
        return tweet_text;
    }

    public void setTweet_text(String tweet_text) {
        this.tweet_text = tweet_text;
    }

    public Bitmap getCover_pic() {
        return cover_pic;
    }

    public void setCover_pic(Bitmap cover_pic) {
        this.cover_pic = cover_pic;
    }

    public Bitmap getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(Bitmap profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public String getTweets() {
        return tweets;
    }

    public void setTweets(String tweets) {
        this.tweets = tweets;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

