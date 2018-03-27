package com.example.absol.riffa;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_INTERNET_PERMISSION = 200;
    private boolean permissionToInternetAccepted = false;
    private String [] permissions = {Manifest.permission.INTERNET};
    private FirebaseAuth auth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private static final String TAG = "Patrik";

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_INTERNET_PERMISSION:
                permissionToInternetAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToInternetAccepted) finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        ActivityCompat.requestPermissions(this, permissions, REQUEST_INTERNET_PERMISSION);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                  //      .setAction("Action", null).show();
                Intent fabIntent = new Intent(MainActivity.this, AudioRecord.class);
                startActivity(fabIntent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // FIND NAV_HEADER_MAIN LAYOUT FOR TEXTVIEWS???????????????????????????????????????????????????????????????????????

      //  LayoutInflater mInflater = getLayoutInflater();
       // View mView = mInflater.inflate(R.layout.nav_header_main, null);
        TextView nameView = navigationView.findViewById(R.id.drawer_name);
        TextView emailView = navigationView.findViewById(R.id.drawer_email);

        emailView.setText("hej");
        nameView.setText("hejhej");

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        } else if (id == R.id.action_logout) {
            auth.signOut();
            Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(logoutIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_record) {
            Intent recordIntent = new Intent(MainActivity.this, AudioRecord.class);
            startActivity(recordIntent);
        } else if (id == R.id.nav_gallery) {
            Intent galleryIntent = new Intent(MainActivity.this, GalleryActivity.class);
            startActivity(galleryIntent);
        } else if (id == R.id.nav_favorites) {
            Intent favoritesIntent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(favoritesIntent);
        } else if (id == R.id.nav_contacts) {
            Intent contactsIntent = new Intent(MainActivity.this, ContactsActivity.class);
            startActivity(contactsIntent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_messages) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
