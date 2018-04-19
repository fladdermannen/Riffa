package com.example.absol.riffa;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity implements ContactsAdapter.ContactsAdapterListener, ContactListAdapter.ContactListAdapterListener{
    private ArrayList<User> contactList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SearchView searchView;
    private ContactsAdapter mAdapter;

    private Dialog mDialog;
    private TextView xClose;
    private SearchView mSearchView;
    private RecyclerView mUserSearchRecyvlerView;
    private ContactListAdapter mUserSearchAdapter;

    ArrayList<User> userList = new ArrayList<>();

    private static final String TAG = "Patrik";

    DatabaseReference mRef;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.contacts_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mDialog = new Dialog(this);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Contacts");

        prepareContactsData();
        //prepareUsersData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.contacts_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup();
            }
        });


        recyclerView = (RecyclerView) findViewById(R.id.contacts_recycler_view);
        mAdapter = new ContactsAdapter(this, contactList, this);
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
    public void onPause() {
        mAdapter.deleteItems();
        super.onPause();
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
    public void onContactSelected(User user) {
        Toast.makeText(getApplicationContext(), "Selected: " + user.getFullName() + ", " + user.getEmail(), Toast.LENGTH_LONG).show();
        onUserSelected(user);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contacts, menu);

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

    private void prepareContactsData() {
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange CONTACTS: checking database");
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showData(DataSnapshot dataSnapshot) {
        contactList.clear();
        for(DataSnapshot ds : dataSnapshot.getChildren()) {
            User user = ds.getValue(User.class);
            contactList.add(user);
            Log.d(TAG, "showData: GALLERY" + contactList.toString());
        }

        recyclerView.setAdapter(mAdapter);
    }


    private void showPopup() {
        mDialog.setContentView(R.layout.popup_contactsearch);
        xClose = mDialog.findViewById(R.id.dialogclose);
        mSearchView = mDialog.findViewById(R.id.searchView);
        mUserSearchRecyvlerView = mDialog.findViewById(R.id.usersearch_recyclerview);

        mUserSearchAdapter = new ContactListAdapter(this, userList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mUserSearchRecyvlerView.setLayoutManager(mLayoutManager);
        mUserSearchRecyvlerView.setItemAnimator(new DefaultItemAnimator());
        mUserSearchRecyvlerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mUserSearchRecyvlerView.setAdapter(mUserSearchAdapter);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        mSearchView.setMaxWidth(Integer.MAX_VALUE);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mUserSearchAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mUserSearchAdapter.getFilter().filter(query);
                return false;
            }
        });
        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.setIconified(false);
            }
        });

        xClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.d("Patrik", "onDismiss: ");
                mUserSearchAdapter.clearUserList();
            }});

        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.show();
    }

    @Override
    public void onUserSelected(User user) {
        Toast.makeText(this, user.getfName() + " selected", Toast.LENGTH_SHORT).show();
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);

        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);
    }
}
