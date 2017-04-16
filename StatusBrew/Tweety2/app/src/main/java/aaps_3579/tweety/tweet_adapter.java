package aaps_3579.tweety;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by HP_PC on 15-04-2017.
 */
public class tweet_adapter extends RecyclerView.Adapter<tweet_adapter.MyViewHolder> {
    private List<Tweet> tweetList;

    public tweet_adapter(List<Tweet> tweetList)

    {
        this.tweetList = tweetList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.singlerow_recycler, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Tweet tweet = tweetList.get(position);
        holder.profile.setImageBitmap(tweet.getProfile_pic());
        holder.name.setText(tweet.getUsername());
        holder.tweet.setText(tweet.getTweet_text());
    }

    @Override
    public int getItemCount() {
        return tweetList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView profile;
        TextView name, tweet;

        public MyViewHolder(View view) {
            super(view);

            profile = (ImageView) view.findViewById(R.id.profile_pic);
            name = (TextView) view.findViewById(R.id.name);
            tweet = (TextView) view.findViewById(R.id.text);
        }

    }


}
