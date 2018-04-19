package com.example.absol.riffa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "Patrik";
    private User user;
    private ArrayList<Recording> recordingsList = new ArrayList<>();

    private TextView name, recordings, email;

    DatabaseReference mRef;
    FirebaseAuth auth;
    FirebaseUser mUser;

    DatabaseReference addUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        user = (User)bundle.getSerializable("user");
        String mFullName = user.getfName() + " " + user.getlName();
        String mEmail = user.getEmail();
        name = findViewById(R.id.user_profile_name);
        email = findViewById(R.id.user_profile_email);
        recordings = findViewById(R.id.user_profile_recordings);
        name.setText(mFullName);
        email.setText(mEmail);

        String userID = user.getuID();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Recordings");
        prepareRecordingData();

        auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();
    }


    public void prepareRecordingData() {

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recordingsList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Recording rec = ds.getValue(Recording.class);
                    recordingsList.add(rec);
                    Log.d(TAG, "onDataChange: " + recordingsList.toString());
                }
                recordings.setText(String.valueOf(recordingsList.size()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void followUser(View v) {
        final ArrayList<String> uidList = new ArrayList<>();
        addUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid()).child("Contacts");


        Query checkQuery = addUserRef.orderByChild("uID");
        checkQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uidList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("uID").getValue().equals(user.getuID())) {
                        uidList.add(String.valueOf(ds.child("uID").getValue()));
                    }
                }
                Log.d(TAG, "onDataChange: " + uidList);
                if(uidList.size() == 0) {
                    addUserRef.push().setValue(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
