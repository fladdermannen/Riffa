package com.example.absol.riffa;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity implements ContactsAdapter.ContactsAdapterListener, ContactListAdapter.ContactListAdapterListener{
    private ArrayList<Contact> contactList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SearchView searchView;
    private ContactsAdapter mAdapter;

    private Dialog mDialog;
    private TextView xClose;
    private SearchView mSearchView;
    private RecyclerView mUserSearchRecyvlerView;
    private ContactListAdapter mUserSearchAdapter;

    ArrayList<User> userList = new ArrayList<>();

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
        recyclerView.setAdapter(mAdapter);
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
    public void onContactSelected(Contact contact) {
        Toast.makeText(getApplicationContext(), "Selected: " + contact.getName() + ", " + contact.getEmail(), Toast.LENGTH_LONG).show();
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
        Contact con = new Contact("Perra", "per@hotmail.com");
        contactList.add(con);

        con = new Contact("Berra", "email.com@.com");
        contactList.add(con);

        con = new Contact("Ferra", "email.com@.com");
        contactList.add(con);

        con = new Contact("Lerra", "email.com@.com");
        contactList.add(con);

        con = new Contact("Gerra", "email.com@.com");
        contactList.add(con);
    }

    private void prepareUsersData() {
        User user = new User("Gustav", "II Adolf", "kung@mail.se", "123");
        userList.add(user);
        user = new User("Karl", "XII", "kung@mail.se", "123");
        userList.add(user);
        user = new User("Gustav", "Vasa", "kung@mail.se", "123");
        userList.add(user);
        user = new User("Henry", "VIII", "kung@mail.se", "123");
        userList.add(user);
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
            }
        });

        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.show();
    }

    @Override
    public void onUserSelected(User user) {
        Toast.makeText(this, user.getfName() + " selected", Toast.LENGTH_SHORT).show();
    }
}
