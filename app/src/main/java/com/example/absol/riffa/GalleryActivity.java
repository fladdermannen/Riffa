package com.example.absol.riffa;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity implements RecordingsAdapter.RecordingsAdapterListener{

    private ArrayList<Recording> recordingList = new ArrayList<>();
    private ArrayList<Recording> recordingsMakePrivate = new ArrayList<>();
    private ArrayList<Recording> recordingsMakePublic = new ArrayList<>();
    private ArrayList<Recording> myFavorites = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecordingsAdapter mAdapter;
    private SearchView searchView;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    private static final String TAG = "Patrik";
    private FirebaseAuth auth;
    private String userID;
    private boolean lock;
    TextView textView;
    TextView textViewDelete;
    Dialog deleteDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textView = findViewById(R.id.textViewHidden);
        lock = false;
        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance();
        deleteDialog = new Dialog(this);
        recordingsMakePrivate.clear();
        recordingsMakePublic.clear();
        loadFavorites();

        mReference= mDatabase.getReference().child("Users").child(userID).child("Recordings");
        prepareRecordingData();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Log.d(TAG, "onCreate: GALLERY current recordingslist: " + recordingList);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fabIntent = new Intent(GalleryActivity.this, AudioRecord.class);
                startActivity(fabIntent);
            }
        });


        recyclerView = (RecyclerView) findViewById(R.id.gallery_recycler_view);
        mAdapter = new RecordingsAdapter(this, recordingList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        ItemTouchHelper.SimpleCallback itemtouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                mAdapter.onItemRemove(viewHolder, recyclerView);
            }
        };

        new ItemTouchHelper(itemtouchHelperCallback).attachToRecyclerView(recyclerView);
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: Gallery destroyed");
        super.onDestroy();
    }
    @Override
    public void onPause() {
        mAdapter.deleteItems();
        makeAccessPrivate();
        makeAccessPublic();
        super.onPause();
    }

    @Override
    public void onResume() {
        lock = false;
        super.onResume();
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

            Intent intent = new Intent(this, MyMediaPlayer.class);
            intent.putExtras(bundle);
            intent.putExtras(bundle2);
            intent.putExtras(bundle3);
            intent.putExtra("position", recordingList.indexOf(rec));

            addClick(rec);

            startActivity(intent);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gallery, menu);


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public void accessChange(Recording rec, View view) {
        boolean check = rec.getAccess();
        if(!check) {
            rec.setAccess(true);
            view.setBackgroundResource(R.drawable.ic_lock_unlocked);
            recordingsMakePublic.add(rec);
            recordingsMakePrivate.remove(rec);
            Snackbar snackbar = Snackbar.make(recyclerView, "Access set to public.\nAnyone can find your recording.", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
        else {
            rec.setAccess(false);
            view.setBackgroundResource(R.drawable.ic_lock);
            recordingsMakePrivate.add(rec);
            recordingsMakePublic.remove(rec);
            Snackbar snackbar = Snackbar.make(recyclerView, "Access set to private.", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    public void prepareRecordingData() {

        mReference.addValueEventListener(new ValueEventListener() {
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
            recordingList.add(rec);
            Log.d(TAG, "showData: GALLERY" + recordingList.toString());

        }
        if(recordingList.size()==0) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.INVISIBLE);
        }
        recyclerView.setAdapter(mAdapter);
    }

    private void loadFavorites() {
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Favorites");
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

    private void makeAccessPublic() {
        if(recordingsMakePublic.size() > 0) {
            for(Recording rec : recordingsMakePublic) {
                String key = rec.getKey();
                Query publicQuery = mReference.orderByChild("key").equalTo(key);

                publicQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot publicSnapshot : dataSnapshot.getChildren()) {
                            publicSnapshot.getRef().child("access").setValue(true);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled: ", databaseError.toException());
                    }
                });

                DatabaseReference clickRef = FirebaseDatabase.getInstance().getReference().child("ClickCounters");
                Query clickQuery = clickRef.orderByChild("key").equalTo(key);

                clickQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            ds.getRef().child("access").setValue(true);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                DatabaseReference recentRef = FirebaseDatabase.getInstance().getReference().child("Recent");
                Query recentQuery = recentRef.orderByChild("key").equalTo(key);

                recentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            ds.getRef().child("access").setValue(true);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    private void makeAccessPrivate() {
        if(recordingsMakePrivate.size() > 0) {
            for(Recording rec : recordingsMakePrivate) {
                String key = rec.getKey();
                Query privateQuery = mReference.orderByChild("key").equalTo(key);

                privateQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot privateSnapshot : dataSnapshot.getChildren()) {
                            privateSnapshot.getRef().child("access").setValue(false);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled: ", databaseError.toException());
                    }
                });

                DatabaseReference clickRef = FirebaseDatabase.getInstance().getReference().child("ClickCounters");
                Query clickQuery = clickRef.orderByChild("key").equalTo(key);

                clickQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            ds.getRef().child("access").setValue(false);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                DatabaseReference recentRef = FirebaseDatabase.getInstance().getReference().child("Recent");
                Query recentQuery = recentRef.orderByChild("key").equalTo(key);

                recentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            ds.getRef().child("access").setValue(false);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
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
