package com.example.absol.riffa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserGalleryActivity extends AppCompatActivity implements UserGalleryAdapter.UserGalleryAdapterListener{

    private static final String TAG = "Patrik";

    private RecyclerView recyclerView;
    private TextView textView;
    private ArrayList<Recording> recordingList = new ArrayList<>();
    private ArrayList<Recording> myFavorites = new ArrayList<>();
    private UserGalleryAdapter mAdapter;

    private User user;

    DatabaseReference mRef;

    private boolean lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_gallery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lock = false;
        textView = findViewById(R.id.textViewHidden);
        loadFavorites();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        user = (User)bundle.getSerializable("user");
        mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getuID()).child("Recordings");
        prepareRecordingData();


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.user_gallery_recyclerView);
        mAdapter = new UserGalleryAdapter(this, recordingList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }

    @Override
    public void onRecordingSelected(Recording rec) {
        if(!lock) {
            lock = true;

            Bundle bundle = new Bundle();
            bundle.putSerializable("recordings", recordingList);
            Bundle bundle2 = new Bundle();
            bundle2.putSerializable("current", rec);
            Bundle bundle3 = new Bundle();
            bundle3.putSerializable("favorites", myFavorites);

            Intent intent = new Intent(this, UserMediaPlayer.class);
            intent.putExtras(bundle);
            intent.putExtras(bundle2);
            intent.putExtras(bundle3);
            intent.putExtra("position", recordingList.indexOf(rec));

            addClick(rec);
            startActivity(intent);
        }

    }

    @Override
    public void onResume() {
        lock = false;
        super.onResume();
    }

    public void prepareRecordingData() {

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange GALLERY: checking database");
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showData(DataSnapshot dataSnapshot) {
        recordingList.clear();
        for(DataSnapshot ds : dataSnapshot.getChildren()) {
            Recording rec = ds.getValue(Recording.class);
            if(rec.getAccess()) {
                recordingList.add(rec);
                Log.d(TAG, "showData: GALLERY" + recordingList.toString());
            }
        }
        if(recordingList.size()==0) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.INVISIBLE);
        }
        recyclerView.setAdapter(mAdapter);
    }

    private void loadFavorites() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("Favorites");
        favoritesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showFavorites(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showFavorites(DataSnapshot ds) {
        myFavorites.clear();
        for(DataSnapshot snap : ds.getChildren()) {
            Recording rec = snap.getValue(Recording.class);
            myFavorites.add(rec);
        }
        Log.d(TAG, "onDataChange: favoriteslist" + myFavorites);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_gallery, menu);
        //getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_close) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if(id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void addClick(Recording rec) {
        String key = rec.getKey();

        DatabaseReference clickReference = FirebaseDatabase.getInstance().getReference().child("ClickCounters");
        Query clickQuery = clickReference.orderByChild("key").equalTo(key);

        clickQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    ClickCounter cc = ds.getValue(ClickCounter.class);
                    int clicks = cc.getClicks();
                    ds.getRef().child("clicks").setValue(clicks+1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
