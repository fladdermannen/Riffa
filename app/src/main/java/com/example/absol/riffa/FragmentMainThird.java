package com.example.absol.riffa;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Collections;

public class FragmentMainThird extends Fragment {
    View view;
    SwipeRefreshLayout swipeLayout;
    CardView card1, card2, card3, card4, card5, card6, card7, card8, card9;
    TextView title1, title2, title3, title4, title5, title6, title7, title8, title9;
    TextView author1, author2, author3, author4, author5, author6, author7, author8, author9;
    private boolean lock;


    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private static final String TAG = "Patrik";

    private ArrayList<ClickCounter> recentCcList = new ArrayList<>();
    private ArrayList<Recording> recentRecordingList = new ArrayList<>();
    private ArrayList<Recording> sortedRecordings = new ArrayList<>();
    private ArrayList<Recording> myFavorites = new ArrayList<>();

    public FragmentMainThird() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_fragment_third,container,false);
        lock = false;

        swipeLayout = view.findViewById(R.id.main_swipelayout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("Patrik", "onRefresh: swipe RECENT refresh called");

                loadData();
                swipeLayout.setRefreshing(false);
            }
        });

        card1 = view.findViewById(R.id.recent_card_first);
        card2 = view.findViewById(R.id.recent_card_second);
        card3 = view.findViewById(R.id.recent_card_third);
        card4 = view.findViewById(R.id.recent_card_fourth);
        card5 = view.findViewById(R.id.recent_card_fifth);
        card6 = view.findViewById(R.id.recent_card_sixth);
        card7 = view.findViewById(R.id.recent_card_seventh);
        card8 = view.findViewById(R.id.recent_card_eighth);
        card9 = view.findViewById(R.id.recent_card_ninth);
        title1 = view.findViewById(R.id.recent_title);
        title2 = view.findViewById(R.id.recent_title_second);
        title3 = view.findViewById(R.id.recent_title_third);
        title4 = view.findViewById(R.id.recent_title_fourth);
        title5 = view.findViewById(R.id.recent_title_fifth);
        title6 = view.findViewById(R.id.recent_title_sixth);
        title7 = view.findViewById(R.id.recent_title_seventh);
        title8 = view.findViewById(R.id.recent_title_eighth);
        title9 = view.findViewById(R.id.recent_title_ninth);
        author1 = view.findViewById(R.id.recent_author);
        author2 = view.findViewById(R.id.recent_author_second);
        author3 = view.findViewById(R.id.recent_author_third);
        author4 = view.findViewById(R.id.recent_author_fourth);
        author5 = view.findViewById(R.id.recent_author_fifth);
        author6 = view.findViewById(R.id.recent_author_sixth);
        author7 = view.findViewById(R.id.recent_author_seventh);
        author8 = view.findViewById(R.id.recent_author_eighth);
        author9 = view.findViewById(R.id.recent_author_ninth);

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!lock) {
                    lock = true;
                    openMediaPlayer(0);
                }
            }
        });
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!lock){
                    lock=true;
                    openMediaPlayer(1);
                }
            }
        });
        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!lock){
                    lock=true;
                    openMediaPlayer(2);
                }
            }
        });
        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!lock){
                    lock=true;
                    openMediaPlayer(3);
                }
            }
        });
        card5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!lock){
                    lock=true;
                    openMediaPlayer(4);
                }
            }
        });
        card6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!lock){
                    lock=true;
                    openMediaPlayer(5);
                }
            }
        });
        card7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!lock){
                    lock=true;
                    openMediaPlayer(6);
                }
            }
        });
        card8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!lock){
                    lock=true;
                    openMediaPlayer(7);
                }
            }
        });
        card9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!lock){
                    lock=true;
                    openMediaPlayer(8);
                }
            }
        });

        loadData();
        loadFavorites();

        return view;
    }

    @Override
    public void onResume() {
        lock=false;
        super.onResume();
    }

    private void loadData() {
        recentCcList.clear();

        Query loadQuery = mRef.child("Recent").orderByChild("time");
        loadQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    ClickCounter newCC = ds.getValue(ClickCounter.class);
                    if(newCC.getAccess()) {
                        recentCcList.add(newCC);
                        Log.d(TAG, "onDataChange: ADDED CC IN LIST");
                    }

                }
                loadRecordings();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadRecordings() {
        Log.d(TAG, "loadRecordings: LOAD RECORDINGS CALLED");
        recentRecordingList.clear();
        Collections.reverse(recentCcList);

        for(ClickCounter cc : recentCcList) {
            String UIdInList = cc.getUserId();
            String keyInList = cc.getKey();

            Query recQuery = mRef.child("Users").child(UIdInList).child("Recordings").orderByChild("key").equalTo(keyInList);
            recQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        recentRecordingList.add(ds.getValue(Recording.class));

                        if(recentRecordingList.size()==9)
                            sortRecordings();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void sortRecordings() {
        sortedRecordings.clear();

        for(ClickCounter cc : recentCcList) {
            for(Recording rec : recentRecordingList) {
                if(cc.getKey().equals(rec.getKey()))
                    sortedRecordings.add(rec);
            }
        }

        loadUI();
    }

    private void loadUI() {

        Log.d(TAG, "onDataChange: recording list" + sortedRecordings.get(0).getTitle() + sortedRecordings.get(1).getTitle());
        title1.setText(sortedRecordings.get(0).getTitle());
        author1.setText(sortedRecordings.get(0).getAuthor());

        title2.setText(sortedRecordings.get(1).getTitle());
        author2.setText(sortedRecordings.get(1).getAuthor());

        title3.setText(sortedRecordings.get(2).getTitle());
        author3.setText(sortedRecordings.get(2).getAuthor());

        title4.setText(sortedRecordings.get(3).getTitle());
        author4.setText(sortedRecordings.get(3).getAuthor());

        title5.setText(sortedRecordings.get(4).getTitle());
        author5.setText(sortedRecordings.get(4).getAuthor());

        title6.setText(sortedRecordings.get(5).getTitle());
        author6.setText(sortedRecordings.get(5).getAuthor());

        title7.setText(sortedRecordings.get(6).getTitle());
        author7.setText(sortedRecordings.get(6).getAuthor());

        title8.setText(sortedRecordings.get(7).getTitle());
        author8.setText(sortedRecordings.get(7).getAuthor());

        title9.setText(sortedRecordings.get(8).getTitle());
        author9.setText(sortedRecordings.get(8).getAuthor());

        swipeLayout.setRefreshing(false);
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

    private void openMediaPlayer(int i) {
        if(sortedRecordings.size() >= (i+1)) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("recordings", sortedRecordings);
            Bundle bundle2 = new Bundle();
            bundle2.putSerializable("current", sortedRecordings.get(i));
            Bundle bundle3 = new Bundle();
            bundle3.putSerializable("favorites", myFavorites);

            Intent intent = new Intent(getActivity(), UserMediaPlayer.class);
            intent.putExtras(bundle);
            intent.putExtras(bundle2);
            intent.putExtras(bundle3);
            intent.putExtra("position", i);

            addClick(sortedRecordings.get(i));
            startActivity(intent);
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
