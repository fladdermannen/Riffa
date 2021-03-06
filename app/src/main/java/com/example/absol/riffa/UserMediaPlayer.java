package com.example.absol.riffa;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
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
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

public class UserMediaPlayer extends AppCompatActivity {

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
    private static ArrayList<Recording> addFavorites = new ArrayList<>();
    private static ArrayList<Recording> removeFavorites = new ArrayList<>();
    private ArrayList<String> favoritesKeyList = new ArrayList<>();
    private static ArrayList<Recording> myFavorites = new ArrayList<>();
    private static ArrayList<Recording> favoritesChecker = new ArrayList<>();

    private ImageView iv;
    private static MediaPlayer mPlayer = null;

    private static boolean stopHandler = false;
    private static Handler mSeekbarUpdateHandler;
    private static Runnable mUpdateSeekbar;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    int currentPosition;

    private static final String TAG = "Patrik";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        stopHandler = false;
        addFavorites.clear();
        removeFavorites.clear();
        favoritesKeyList.clear();
        favoritesChecker.clear();

        mRef = FirebaseDatabase.getInstance().getReference();


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        recordings = (ArrayList<Recording>) bundle.getSerializable("recordings");
        if(bundle.getSerializable("favorites")!=null) {
            myFavorites = (ArrayList<Recording>) bundle.getSerializable("favorites");
        }
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

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
            }
        });


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
        getMenuInflater().inflate(R.menu.menu_user_media_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home :
                onBackPressed();
                return true;
            case R.id.action_close :
                startActivity(new Intent(this, MainActivity.class));
        }

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

        private TextView textGenre, textTitle;
        public Chronometer chrono2;
        private ImageButton bPlayPause, bStop;
        private ImageButton bFavorite;
        private SeekBar seekbar;
        private int duration;
        private boolean isFavorite;

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mRef;
        private String userId;

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

            int current = getArguments().getInt(ARG_SECTION_NUMBER)-1;
            final Recording rec = recordings.get(current);
            final String url = rec.getLink();
            userId = user.getUid();
            mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Recordings").child(rec.getKey());

            isFavorite = false;
            duration = rec.getLength();
            bPlayPause = rootView.findViewById(R.id.btnPlayPause);
            bStop = rootView.findViewById(R.id.btnStop);
            bFavorite = (ImageButton) rootView.findViewById(R.id.btnFavorite);

            textGenre = rootView.findViewById(R.id.textView2);
            chrono2 = rootView.findViewById(R.id.chrono2);
            textTitle = rootView.findViewById(R.id.text_title);

            seekbar = rootView.findViewById(R.id.seekBar);
            seekbar.setMax(duration);

            bPlayPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mPlayer == null || (mPlayer != null && !mPlayer.isPlaying())) {
                        startPlaying(url);
                    }
                }
            });

            bStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mPlayer.isPlaying() && mPlayer != null) {
                        mPlayer.stop();
                    }
                }
            });

            Log.d(TAG, "onCreateView: HANDLER HERE");
            mSeekbarUpdateHandler = new Handler();
            mUpdateSeekbar = new Runnable() {
                @Override
                public void run() {
                    if(!stopHandler) {
                        seekbar.setProgress(mPlayer.getCurrentPosition());
                        mSeekbarUpdateHandler.postDelayed(this, 50);
                        if(!mPlayer.isPlaying()) {
                            seekbar.setProgress(0);
                        }
                    }
                }
            };

            mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 0);

            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser)
                        mPlayer.seekTo(progress);
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            Log.d(TAG, "onCreateView: my current favorites" +  myFavorites);
            for(Recording fav : myFavorites) {
                if(rec.getKey().equals(fav.getKey())) {
                    isFavorite = true;
                }
            }

            bFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isFavorite = !isFavorite;
                    if(isFavorite) {
                        bFavorite.setImageResource(R.drawable.ic_star_rate_black_18dp);
                        addFavorites.add(rec);
                        removeFavorites.remove(rec);
                    } else {
                        bFavorite.setImageResource(R.drawable.ic_star_border_black_18dp);
                        addFavorites.remove(rec);
                        removeFavorites.add(rec);
                    }
                }
            });

            if(isFavorite) {
                bFavorite.setImageResource(R.drawable.ic_star_rate_black_18dp);
            } else {
                bFavorite.setImageResource(R.drawable.ic_star_border_black_18dp);
            }

            textTitle.setText(rec.getTitle());
            chrono2.setBase(SystemClock.elapsedRealtime() - (rec.getLength()) );
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
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }

        super.onStop();
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

        mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 0);

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
            }
        });
    }


    private static void stopPlaying() {
        if(mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    public interface Callback {
        void onPageChanged();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: called");
        stopHandler = true;
        addFavorites();
        removeFavorites();
        super.onPause();
    }

    public void addFavorites() {
        if(addFavorites.size() > 0) {
            String uid = user.getUid();
            for(final Recording rec : addFavorites) {
                final String key = rec.getKey();


                Query favoriteFolderQuery = mRef.child("Users").child(uid).child("Favorites").orderByChild("key").equalTo(key);
                favoriteFolderQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        favoritesKeyList.clear();
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            if(ds.child("key").getValue().equals(key)) {
                                favoritesKeyList.add(String.valueOf(ds.child("key").getValue()));
                            }
                        }
                        if (favoritesKeyList.size() == 0) {
                            mRef.child("Users").child(user.getUid()).child("Favorites").push().setValue(rec);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    public void removeFavorites() {
        if(removeFavorites.size() > 0) {
            String uid = user.getUid();
            for(Recording rec : removeFavorites) {
                String key = rec.getKey();

                Query favoriteFolderQuery = mRef.child("Users").child(uid).child("Favorites").orderByChild("key").equalTo(key);
                favoriteFolderQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            ds.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }
/*
    private static void loadFavorites() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Favorites");
        favoritesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static void showData(DataSnapshot ds) {
        myFavorites.clear();

        for (DataSnapshot snap : ds.getChildren()) {
            Recording rec = snap.getValue(Recording.class);
            myFavorites.add(rec);
        }
        Log.d(TAG, "onDataChange: my favorites are " + myFavorites);
    }
*/
}
