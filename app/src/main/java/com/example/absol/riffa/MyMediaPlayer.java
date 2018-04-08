package com.example.absol.riffa;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class MyMediaPlayer extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Recording currentRec;
    private static ArrayList<Recording> recordings = new ArrayList<>();
    private static Button bPause, bForward, bBack, bRewind;
    private ImageView iv;
    private static MediaPlayer mPlayer = null;

    private double startTime = 0;
    private double finalTime = 0;

    private Handler mHandler = new Handler();
    private int forwardTime = 5000;
    private int backwardTime = 5000;

    private int oneTimeOnly = 0;

    int currentPosition;

    private static final String TAG = "Patrik";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        recordings = (ArrayList<Recording>) bundle.getSerializable("recordings");
        currentRec = (Recording) bundle.getSerializable("current");
        final int currentPositionChecker = intent.getIntExtra("position", -1);
        currentPosition = intent.getIntExtra("position", -1);

        Log.d(TAG, "onCreate: index of current rec is " + currentPosition);

        mPlayer = new MediaPlayer();
        String url = currentRec.getLink();
        try {
            mPlayer.setDataSource(url);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Create the adapter that will return a fragment for each of the
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(currentPosition);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private int currentPage = currentPositionChecker;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Fragment fragment = mSectionsPagerAdapter.getItem(currentPage);

                if(fragment instanceof Callback && currentPage != position) {
                    ((Callback)mSectionsPagerAdapter.getItem(currentPage)).onPageChanged();
                }
                currentPage = position;
                currentRec = recordings.get(position);
                Log.d(TAG, "onPageSelected: current position is " + currentPage + " and current recording is " + currentRec);
                // ----------------
                String url = recordings.get(currentPage).getLink();
                startPlaying(url);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        Log.d(TAG, "onCreate:MEDIAPLAYER " + recordings);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_media_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements Callback{
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        private SeekBar seekbar;
        private TextView textGenre, textLength, textExtra, textTitle;


        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_media_player, container, false);


            bPause = rootView.findViewById(R.id.btnPause);
            bForward = rootView.findViewById(R.id.btnForward);
            bBack = rootView.findViewById(R.id.btnBack);
            bRewind = rootView.findViewById(R.id.btnRewind);

            textGenre = rootView.findViewById(R.id.textView2);
            textLength = rootView.findViewById(R.id.textView3);
            textExtra = rootView.findViewById(R.id.textView4);
            textTitle = rootView.findViewById(R.id.text_title);

            seekbar = rootView.findViewById(R.id.seekBar);
            seekbar.setClickable(false);
            bPause.setEnabled(false);

            int current = getArguments().getInt(ARG_SECTION_NUMBER)-1;
            Recording rec = recordings.get(current);
            textTitle.setText(rec.getTitle());
            textLength.setText(String.valueOf(rec.getLength()));
            textGenre.setText(rec.getGenre());

            return rootView;
        }

        @Override
        public void onPageChanged() {
            stopPlaying();
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }


        @Override
        public int getCount() {
            // Show X total pages.
            return recordings.size();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    private static void startPlaying(String url) {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(url);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    public interface Callback {
        void onPageChanged();
    }
}
