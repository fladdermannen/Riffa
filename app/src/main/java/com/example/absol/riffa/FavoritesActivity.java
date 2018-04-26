package com.example.absol.riffa;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity implements FavoritesAdapter.FavoritesAdapterListener{

    private ArrayList<Recording> favoritesList = new ArrayList<>();
    private ArrayList<Recording> favoritesToDelete = new ArrayList<>();
    private RecyclerView recyclerView;
    private FavoritesAdapter mAdapter;
    private SearchView searchView;
    private TextView textViewEmpty;
    DatabaseReference mReference;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String userId;
    private static final String TAG = "Patrik";

    private boolean lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lock = false;
        favoritesToDelete.clear();

        textViewEmpty = findViewById(R.id.textViewHidden);

        userId = user.getUid();
        mReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Favorites");

        prepareRecordingData();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = (RecyclerView) findViewById(R.id.favorites_recycler_view);
        mAdapter = new FavoritesAdapter(this, favoritesList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onFavoriteSelected(Recording rec) {
        if(!lock) {
            lock = true;

            Bundle bundle = new Bundle();
            bundle.putSerializable("recordings", favoritesList);
            Bundle bundle2 = new Bundle();
            bundle2.putSerializable("current", rec);
            Bundle bundle3 = new Bundle();
            bundle3.putSerializable("favorites", favoritesList);

            Intent intent = new Intent(this, MyMediaPlayer.class);
            intent.putExtras(bundle);
            intent.putExtras(bundle2);
            intent.putExtras(bundle3);
            intent.putExtra("position", favoritesList.indexOf(rec));

            addClick(rec);
            startActivity(intent);
        }
    }

    @Override
    public void onPause() {
        deleteFavorites();
        favoritesToDelete.clear();
        super.onPause();
    }

    @Override
    public void onResume() {
        lock = false;
        super.onResume();
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
        getMenuInflater().inflate(R.menu.menu_favorites, menu);


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

    public void prepareRecordingData() {

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange FAVORITES: checking database");
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showData(DataSnapshot dataSnapshot) {
        favoritesList.clear();

        for(DataSnapshot ds : dataSnapshot.getChildren()) {
            final Recording rec = ds.getValue(Recording.class);

            if(!rec.getAuthor().equals(user.getDisplayName())) {
                Log.d(TAG, "showData: checking others rec if available");
                 StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(rec.getStorageName());
                 storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                     @Override
                     public void onSuccess(Uri uri) {
                         Log.d(TAG, "onSuccess: recording found");
                         favoritesList.add(rec);
                         recyclerView.setAdapter(mAdapter);
                         if(favoritesList.size()==0) {
                             textViewEmpty.setVisibility(View.VISIBLE);
                         } else {
                             textViewEmpty.setVisibility(View.INVISIBLE);
                         }
                     }
                 }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         Log.d(TAG, "onFailure: recording not found");
                         favoritesToDelete.add(rec);
                     }
                 });
            } else {
                favoritesList.add(rec);
                Log.d(TAG, "showData: added my own favorite");
            }

            Log.d(TAG, "showData: GALLERY" + favoritesList.toString());

        }
        if(favoritesList.size()==0) {
            textViewEmpty.setVisibility(View.VISIBLE);
        } else {
            textViewEmpty.setVisibility(View.INVISIBLE);
        }
        recyclerView.setAdapter(mAdapter);
    }

    private void deleteFavorites() {
        Log.d(TAG, "deleteFavorites: size is " + favoritesToDelete.size());
        if(favoritesToDelete.size() > 0) {
            for (Recording rec : favoritesToDelete) {
                String key = rec.getKey();
                Query deleteQuery = mReference.orderByChild("key").equalTo(key);
                deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snap : dataSnapshot.getChildren()) {
                            snap.getRef().removeValue();
                            Log.d(TAG, "onDataChange: favorite deleted");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: delete failed");
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