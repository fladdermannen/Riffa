package com.example.absol.riffa;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity implements RecordingsAdapter.RecordingsAdapterListener{

    private ArrayList<Recording> recordingList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecordingsAdapter mAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fabIntent = new Intent(GalleryActivity.this, AudioRecord.class);
                startActivity(fabIntent);
            }
        });

       // prepareRecordingData();

        recyclerView = (RecyclerView) findViewById(R.id.gallery_recycler_view);
        mAdapter = new RecordingsAdapter(this, recordingList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onRecordingSelected(Recording rec) {
        Toast.makeText(getApplicationContext(), "Selected: " + rec.getTitle() + ", " + rec.getAccess(), Toast.LENGTH_LONG).show();
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
