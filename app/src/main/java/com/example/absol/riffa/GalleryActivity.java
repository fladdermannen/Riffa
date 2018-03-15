package com.example.absol.riffa;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private List<Recording> recordingList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecordingsAdapter mAdapter;

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


        recyclerView = (RecyclerView) findViewById(R.id.gallery_recycler_view);
        mAdapter = new RecordingsAdapter(recordingList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        prepareRecordingData();

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

        rec = new Recording("Hejhej", "3.33", "fixit");
        recordingList.add(rec);

        rec = new Recording("New", "4.20", "cool");
        recordingList.add(rec);

        rec = new Recording("Blabla", "5.55", "trash");
        recordingList.add(rec);

        rec = new Recording("Banana banana", "4.20", "oijasdio");
        recordingList.add(rec);

        rec = new Recording("schoolboy", "4.33", "meshuggah");
        recordingList.add(rec);

        rec = new Recording("Hejhej", "3.33", "fix");
        recordingList.add(rec);

        rec = new Recording("New", "4.20", "cool");
        recordingList.add(rec);

        rec = new Recording("Blabla", "5.55", "trash");
        recordingList.add(rec);

        rec = new Recording("Banana banana", "4.20", "oijasdio");
        recordingList.add(rec);

        rec = new Recording("schoolboy", "4.33", "meshuggah");
        recordingList.add(rec);

        rec = new Recording("Hejhej", "3.33", "fix");
        recordingList.add(rec);

    }

    public void backBtnPress(View view) {
        Intent backIntent = new Intent(GalleryActivity.this, MainActivity.class);
        startActivity(backIntent);
    }

}
