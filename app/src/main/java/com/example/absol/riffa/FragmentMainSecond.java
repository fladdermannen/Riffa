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
import java.util.Comparator;

public class FragmentMainSecond extends Fragment {
    View view;
    SwipeRefreshLayout swipeLayout;
    CardView card1, card2, card3, card4, card5, card6, card7, card8, card9;
    TextView title1, title2, title3, title4, title5, title6, title7, title8, title9;
    TextView author1, author2, author3, author4, author5, author6, author7, author8, author9;
    private boolean lock;

    private ArrayList<Recording> mostClickedRecordings = new ArrayList<>();
    private ArrayList<ClickCounter> mostClicks = new ArrayList<>();
    private ArrayList<Recording> myFavorites = new ArrayList<>();
    private ArrayList<Recording> sortedRecordingList = new ArrayList<>();

    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    private static final String TAG = "Patrik";

    public FragmentMainSecond() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_fragment_second,container,false);
        lock = false;


        swipeLayout = view.findViewById(R.id.main_swipelayout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("Patrik", "onRefresh: swipe TOP refresh called");

                loadData();

            }
        });

        card1 = view.findViewById(R.id.top_card_first);
        card2 = view.findViewById(R.id.top_card_second);
        card3 = view.findViewById(R.id.top_card_third);
        card4 = view.findViewById(R.id.top_card_fourth);
        card5 = view.findViewById(R.id.top_card_fifth);
        card6 = view.findViewById(R.id.top_card_sixth);
        card7 = view.findViewById(R.id.top_card_seventh);
        card8 = view.findViewById(R.id.top_card_eighth);
        card9 = view.findViewById(R.id.top_card_ninth);
        title1 = view.findViewById(R.id.top_title);
        title2 = view.findViewById(R.id.top_title_second);
        title3 = view.findViewById(R.id.top_title_third);
        title4 = view.findViewById(R.id.top_title_fourth);
        title5 = view.findViewById(R.id.top_title_fifth);
        title6 = view.findViewById(R.id.top_title_sixth);
        title7 = view.findViewById(R.id.top_title_seventh);
        title8 = view.findViewById(R.id.top_title_eighth);
        title9 = view.findViewById(R.id.top_title_ninth);
        author1 = view.findViewById(R.id.top_author);
        author2 = view.findViewById(R.id.top_author_second);
        author3 = view.findViewById(R.id.top_author_third);
        author4 = view.findViewById(R.id.top_author_fourth);
        author5 = view.findViewById(R.id.top_author_fifth);
        author6 = view.findViewById(R.id.top_author_sixth);
        author7 = view.findViewById(R.id.top_author_seventh);
        author8 = view.findViewById(R.id.top_author_eighth);
        author9 = view.findViewById(R.id.top_author_ninth);

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
        lock = false;
        super.onResume();
    }

    private void loadData() {
        mostClicks.clear();
        Query loadQuery = mRef.child("ClickCounters").orderByChild("access").equalTo(true);

        loadQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ClickCounter cc = ds.getValue(ClickCounter.class);
                    int clicks = cc.getClicks();
                    if(mostClicks.size() < 9) {
                        mostClicks.add(cc);
                        Log.d(TAG, "onDataChange: added cc " + mostClicks);
                    }
                    else{
                        for(ClickCounter ccInList : mostClicks) {
                            int clicksInList = ccInList.getClicks();
                            if(clicks>clicksInList) {
                                mostClicks.remove(ccInList);
                                mostClicks.add(cc);
                                Log.d(TAG, "onDataChange: replaced cc");
                                break;
                            }
                        }
                    }
                }
                Collections.sort(mostClicks, new mClicksComp());
                loadRecordings();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadRecordings() {
        mostClickedRecordings.clear();
        Log.d(TAG, "loadRecordings: MOSTCLICKS SIZE AND ORDER" + mostClicks.get(0).getClicks() + mostClicks.get(1).getClicks() + mostClicks.get(2).getClicks());

        for(ClickCounter clickCounter : mostClicks) {
            String uIDinList = clickCounter.getUserId();
            String keyInList = clickCounter.getKey();
            Query query = mRef.child("Users").child(uIDinList).child("Recordings").orderByChild("key").equalTo(keyInList);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        Recording rec = ds.getValue(Recording.class);
                        mostClickedRecordings.add(rec);
                        Log.d(TAG, "onDataChange: added rec " + mostClickedRecordings);
                    }
                    if(mostClickedRecordings.size()>=9) {
                        sortRecordings();
                        loadUI();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void loadUI() {
        Log.d(TAG, "loadUI: CALLED LOAD UI");
        Log.d(TAG, "onDataChange: recording list" + sortedRecordingList.get(0).getTitle() + sortedRecordingList.get(1).getTitle());
        title1.setText(sortedRecordingList.get(0).getTitle());
        author1.setText(sortedRecordingList.get(0).getAuthor());

        title2.setText(sortedRecordingList.get(1).getTitle());
        author2.setText(sortedRecordingList.get(1).getAuthor());

        title3.setText(sortedRecordingList.get(2).getTitle());
        author3.setText(sortedRecordingList.get(2).getAuthor());

        title4.setText(sortedRecordingList.get(3).getTitle());
        author4.setText(sortedRecordingList.get(3).getAuthor());

        title5.setText(sortedRecordingList.get(4).getTitle());
        author5.setText(sortedRecordingList.get(4).getAuthor());

        title6.setText(sortedRecordingList.get(5).getTitle());
        author6.setText(sortedRecordingList.get(5).getAuthor());

        title7.setText(sortedRecordingList.get(6).getTitle());
        author7.setText(sortedRecordingList.get(6).getAuthor());

        title8.setText(sortedRecordingList.get(7).getTitle());
        author8.setText(sortedRecordingList.get(7).getAuthor());

        title9.setText(sortedRecordingList.get(8).getTitle());
        author9.setText(sortedRecordingList.get(8).getAuthor());

        swipeLayout.setRefreshing(false);
    }





    class mClicksComp implements Comparator<ClickCounter> {

        @Override
        public int compare(ClickCounter cc1, ClickCounter cc2) {
            if(cc1.getClicks() < cc2.getClicks()) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    private void sortRecordings() {
        sortedRecordingList.clear();

        for(ClickCounter cc : mostClicks) {
            for(Recording rec : mostClickedRecordings) {
                if(cc.getKey().equals(rec.getKey())) {
                    sortedRecordingList.add(rec);
                }
            }
        }
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
        if(sortedRecordingList.size() >= (i+1)) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("recordings", sortedRecordingList);
            Bundle bundle2 = new Bundle();
            bundle2.putSerializable("current", sortedRecordingList.get(i));
            Bundle bundle3 = new Bundle();
            bundle3.putSerializable("favorites", myFavorites);

            Intent intent = new Intent(getActivity(), UserMediaPlayer.class);
            intent.putExtras(bundle);
            intent.putExtras(bundle2);
            intent.putExtras(bundle3);
            intent.putExtra("position", i);

            addClick(sortedRecordingList.get(i));
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
