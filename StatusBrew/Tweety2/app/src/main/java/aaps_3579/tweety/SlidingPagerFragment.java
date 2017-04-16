package aaps_3579.tweety;


import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SlidingPagerFragment extends Fragment {

    LinearLayout cover_user;
    ImageView profile_user;
    TextView name_user, handle_user, bio_user, following_user, followers_user, tweet_count, location;
    int page;
    Tweet tweet;

    public SlidingPagerFragment() {
        // Required empty public constructor
    }

    public static SlidingPagerFragment newInstance(int page) {
        SlidingPagerFragment slidingPagerFragment = new SlidingPagerFragment();
        Bundle args = new Bundle();
        args.putInt("page", page);
        slidingPagerFragment.setArguments(args);
        return slidingPagerFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("page");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_sliding_pager, container, false);

        cover_user = (LinearLayout) inflate.findViewById(R.id.cover_user);
        profile_user = (ImageView) inflate.findViewById(R.id.profile_user);
        name_user = (TextView) inflate.findViewById(R.id.name_user);
        handle_user = (TextView) inflate.findViewById(R.id.handle_user);
        bio_user = (TextView) inflate.findViewById(R.id.bio_user);
        followers_user = (TextView) inflate.findViewById(R.id.followers_user);
        following_user = (TextView) inflate.findViewById(R.id.following_user);
        tweet_count = (TextView) inflate.findViewById(R.id.tweet_count);
        location = (TextView) inflate.findViewById(R.id.location);
        try {
            tweet = Helper.tweetList.get(page);
            System.out.println(page + "---" + tweet.getHandle());
            if (tweet.getCover_pic() != null)
                cover_user.setBackgroundDrawable(new BitmapDrawable(tweet.getCover_pic()));
            else
                cover_user.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
            profile_user.setImageBitmap(tweet.getProfile_pic());
            name_user.setText(tweet.getUsername());
            handle_user.setText("@" + tweet.getHandle());
            bio_user.setText(tweet.getBio());
            following_user.setText("Following-" + tweet.getFollowing());
            followers_user.setText("Followers-" + tweet.getFollowers());
            tweet_count.setText("Tweets-" + tweet.getTweets());
            location.setTextSize(20);
            location.setText("\u2316");

            location.append(tweet.getLocation());
            return inflate;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;

        }

    }

}
