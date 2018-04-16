package com.example.absol.riffa;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity implements RecordingsAdapter.RecordingsAdapterListener{

    private ArrayList<Recording> recordingList = new ArrayList<>();
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
        Log.d(TAG, "onDestroy: destroyed");
        super.onDestroy();
    }
    @Override
    public void onPause() {
        mAdapter.deleteItems();
        super.onPause();
    }

    @Override
    public void onRecordingSelected(Recording rec) {
        if(!lock) {
            lock = true;
            Toast.makeText(getApplicationContext(), "Selected: " + rec.getTitle() + ", access is " + rec.getAccess(), Toast.LENGTH_LONG).show();

            Bundle bundle = new Bundle();
            bundle.putSerializable("recordings", recordingList);
            Bundle bundle2 = new Bundle();
            bundle2.putSerializable("current", rec);

            Intent intent = new Intent(this, MyMediaPlayer.class);
            intent.putExtras(bundle);
            intent.putExtras(bundle2);
            intent.putExtra("position", recordingList.indexOf(rec));


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
        }
        else {
            rec.setAccess(false);
            view.setBackgroundResource(R.drawable.ic_lock);
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





    /*private void prepareRecordingData(){
        Recording rec = new Recording("Test", "3.14", "shit", "2018-03-17");
        recordingList.add(rec);

        rec = new Recording("New", "4.20", "cool", "2018-03-12");
        recordingList.add(rec);

        rec = new Recording("Blabla", "5.55", "trash", "2018-03-11");
        recordingList.add(rec);

        rec = new Recording("Banana banana", "4.20", "oijasdio", "2018-01-17");
        recordingList.add(rec);

        rec = new Recording("fuccboi", "4.33", "genre", "2018-03-20");
        recordingList.add(rec);

        rec = new Recording("Hejhej", "3.33", "fibjofab", "2018-01-12");
        recordingList.add(rec);

        rec = new Recording("New", "4.20", "cool", "2018-03-17");
        recordingList.add(rec);

        rec = new Recording("Blabla", "5.55", "trash", "2018-03-17");
        recordingList.add(rec);

        rec = new Recording("Banana banana", "4.20", "oijasdio", "2018-03-30");
        recordingList.add(rec);

        rec = new Recording("crapcrap", "4.33", "mesah", "2018-03-17");
        recordingList.add(rec);

        rec = new Recording("Hejhej", "3.33", "fixxx", "2018-03-17");
        recordingList.add(rec);

        rec = new Recording("New", "4.20", "cool", "2018-03-17");
        recordingList.add(rec);

        rec = new Recording("Blabla", "5.55", "trash", "2018-03-17");
        recordingList.add(rec);

        rec = new Recording("Banana banana", "4.20", "oijasdio", "2018-03-17");
        recordingList.add(rec);

        rec = new Recording("fisk", "4.33", "aqua", "2018-03-17");
        recordingList.add(rec);

        rec = new Recording("Hejhej", "3.33", "fibble", "2018-03-17");
        recordingList.add(rec);

    } */
}
