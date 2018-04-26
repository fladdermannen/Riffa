package com.example.absol.riffa;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

    private ScrollView mLl;
    private LinearLayout userProfileGallery;
    private TextView name, recordings, email;
    private Button mFollowBtn;

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
        mLl = findViewById(R.id.user_profile_ll);
        userProfileGallery = findViewById(R.id.user_profile_gallery);
        mFollowBtn = findViewById(R.id.user_profile_follow);

        String userID = user.getuID();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Recordings");
        prepareRecordingData();

        auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();

        userProfileGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                Intent intent = new Intent(UserProfileActivity.this, UserGalleryActivity.class);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
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
        }

        return super.onOptionsItemSelected(item);
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
                    Snackbar snackbar = Snackbar.make(mLl,"Added " + user.getfName() + " " + user.getlName() + " to contacts!",Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } else {
                    Snackbar snackbar = Snackbar.make(mLl, "You are already following " + user.getfName() + "!", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
