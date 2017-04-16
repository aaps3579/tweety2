package aaps_3579.tweety;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class view_pager extends FragmentActivity {

    ViewPager viewPager;
    int position;
    boolean flag = true;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        Intent result = getIntent();
        int position = result.getIntExtra("position", -1);
        this.position = position;
        System.out.println(this.position + " in constructor");
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setCurrentItem(position);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (position < Helper.tweetList.size()) {
                System.out.println("Called Get Item for" + position);
                SlidingPagerFragment slidingPagerFragment = SlidingPagerFragment.newInstance(position);
                return slidingPagerFragment;
            }
            return null;
        }

        @Override
        public int getCount() {


            return 100;
        }
    }
}
