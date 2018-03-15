package com.example.absol.riffa;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private List<Recording> recordingList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecordingsAdapter mAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fabIntent = new Intent(GalleryActivity.this, AudioRecord.class);
                startActivity(fabIntent);
            }
        });

        prepareRecordingData();

        recyclerView = (RecyclerView) findViewById(R.id.gallery_recycler_view);
        mAdapter = new RecordingsAdapter(recordingList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Recording rec = recordingList.get(position);
                Toast.makeText(getApplicationContext(), rec.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void prepareRecordingData(){
        Recording rec = new Recording("Test", "3.14", "shit");
        recordingList.add(rec);

        rec = new Recording("New", "4.20", "cool");
        recordingList.add(rec);

        rec = new Recording("Blabla", "5.55", "trash");
        recordingList.add(rec);

        rec = new Recording("Banana banana", "4.20", "oijasdio");
        recordingList.add(rec);

        rec = new Recording("fuccboi", "4.33", "genre");
        recordingList.add(rec);

        rec = new Recording("Hejhej", "3.33", "fibjofab");
        recordingList.add(rec);

        rec = new Recording("New", "4.20", "cool");
        recordingList.add(rec);

        rec = new Recording("Blabla", "5.55", "trash");
        recordingList.add(rec);

        rec = new Recording("Banana banana", "4.20", "oijasdio");
        recordingList.add(rec);

        rec = new Recording("crapcrap", "4.33", "mesah");
        recordingList.add(rec);

        rec = new Recording("Hejhej", "3.33", "fixxx");
        recordingList.add(rec);

        rec = new Recording("New", "4.20", "cool");
        recordingList.add(rec);

        rec = new Recording("Blabla", "5.55", "trash");
        recordingList.add(rec);

        rec = new Recording("Banana banana", "4.20", "oijasdio");
        recordingList.add(rec);

        rec = new Recording("fisk", "4.33", "aqua");
        recordingList.add(rec);

        rec = new Recording("Hejhej", "3.33", "fibble");
        recordingList.add(rec);

    }

    public void backBtnPress(View view) {
        Intent backIntent = new Intent(GalleryActivity.this, MainActivity.class);
        startActivity(backIntent);
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
}
