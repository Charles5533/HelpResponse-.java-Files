package com.example.android.help_response;

import android.Manifest;
import androidx.fragment.app.FragmentManager;
//import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

public class MainPageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, LocationListener {

    FirebaseAuth auth;
    FirebaseUser user;
    String user_id;
    DatabaseReference reference;

    TextView t1,t2;
    Button b4_sourceButton;

    private GoogleMap mMap;
    GoogleApiClient client;
    LatLng startLatLng;
    LocationRequest request;
    Location location;
    double latitude;
    double longitude;

    Marker currentMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        auth = FirebaseAuth.getInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        b4_sourceButton = (Button) findViewById(R.id.button4);
        setSupportActionBar(toolbar);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){

            Intent myintent = new Intent(MainPageActivity.this, MainActivity.class);
            startActivity(myintent);
            finish();
        } else{

            user_id = user.getUid();
            reference = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    t1 = findViewById(R.id.name_text);
                    t2 = findViewById(R.id.email_text);
                    t1.setText(name);
                    t2.setText(email);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = new LocationRequest().create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(500);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)

        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);

    }


    @Override
    public void onConnectionFailed(@androidx.annotation.NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

       // LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        if (location == null) {

            Toast.makeText(getApplicationContext(), "Location could not be found", Toast.LENGTH_SHORT).show();

        } else{

            try {

                Location l = new Location(LocationManager.GPS_PROVIDER);

                startLatLng = new LatLng(l.getLatitude(), l.getLongitude());

                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> myaddresses = geocoder.getFromLocation(startLatLng.latitude, startLatLng.longitude,1);
                String address = myaddresses.get(0).getAddressLine(0);
                String city = myaddresses.get(0).getLocality();
                b4_sourceButton.setText(address + ""+city);

                if (currentMarker == null) {

                    MarkerOptions options = new MarkerOptions();
                    options.position(startLatLng);
                    options.title("current posiition");
                    currentMarker = mMap.addMarker(options);

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 15));

                } else {
                    currentMarker.setPosition(startLatLng);

                }
            } catch (Exception e){

            }

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        client.connect();






        //client = new GoogleApiClient.Builder(this)
    // Add a marker in Sydney and move the camera


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
        getMenuInflater().inflate(R.menu.main_page, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_payment) {
            // Handle the camera action
        } else if (id == R.id.nav_trips) {

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_rides) {

        } else if (id == R.id.nav_signout)
        {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user!=null){
                auth.signOut();
                Intent myintent = new Intent(MainPageActivity.this,MainActivity.class);
                startActivity(myintent);
                finish();
            } else {

                Toast.makeText(getApplicationContext(), "User already Signed out", Toast.LENGTH_SHORT).show();
            }

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
