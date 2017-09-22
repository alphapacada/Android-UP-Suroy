package map.mapexample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,ActivityCompat.OnRequestPermissionsResultCallback,
        NavigationView.OnNavigationItemSelectedListener{
    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final String TAG = "MainActivity";

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    private final String[] locations = {"Admin Bldg", "Admin Field", "AS Bldg", "AS Field", "Basketball Court", "Canteen", "Guest House", "Library", "Management Bldg", "Oblation Square", "Undergrad Bldg"};
    private List<Pin> pins;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pins = new ArrayList<>();
        setContentView(R.layout.activity_maps);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        System.out.println("toolbar=" + toolbar);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if(findViewById(R.id.layout_content)!=null) {
            if(savedInstanceState!=null) {
                return;
            }
        }
        /*SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/
        SupportMapFragment fragment;
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        fragment =(SupportMapFragment) fm.findFragmentById(R.id.layout_content);
        if(fragment == null) {
            System.out.println("wala pay map");
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.layout_content, fragment).commit();
            fragment.getMapAsync(this);
        }


       // getSupportFragmentManager().beginTransaction().add(R.id.layout_content, mapFragment).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    public void loadPins() {
        mMap.clear();

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        System.out.println("onMapReady");
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng upcebu = new LatLng(10.322487, 123.898722);
        mMap.addMarker(new MarkerOptions().position(upcebu).title("Marker in UPCEBUs"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(upcebu, 19.61f));
        enableMyLocation();
        // Write a message to the database
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference pinRef = database.child("pins");
        ValueEventListener pinListener = new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String pinName;
                Double pinLat;
                Double pinLong;
                Pin pin;
                mMap.clear();
                // Get Pin object and use the values to update the UI
                for (DataSnapshot pinSnapshot: dataSnapshot.getChildren()) {
                    pinName = (String) pinSnapshot.child("name").getValue();
                    pinLat = (Double) pinSnapshot.child("Lat").getValue();
                    pinLong = (Double) pinSnapshot.child("Long").getValue();
                    pin = new Pin(pinName, pinLat, pinLong);
                    System.out.println(pin);
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(pin.getLatitude(), pin.getLongitude()))
                            .title(pin.getName()));
                }
                //loadPins();
                // ...
            /*    try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }*/

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        pinRef.addValueEventListener(pinListener);
    }
    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
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
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = null;
        if (id == R.id.nav_gallery) {
            System.out.println("Clicked Nav Gallery");
            //Intent galleryIntent = new Intent(this, GalleryActivity.class);
            //startActivity(galleryIntent);
             fragment = (GalleryActivity) getSupportFragmentManager().findFragmentById(R.id.fragment_gallery);
            if(fragment!=null) {
                System.out.println("not Null gallery Fragment");

            } else {
                System.out.println("NUll Gallery Fragment");
                fragment = new GalleryActivity();
                if(findViewById(R.id.layout_content)!=null) {
                    transaction.replace(R.id.layout_content, fragment);

                } else{
                    System.out.println("NUll Layoutcontent");
                }
                if(findViewById(R.id.fragment_gallery) == null) {
                    System.out.println("Still null fragment gallery. NOt replaced.");

                }/*if(findViewById(R.id.map)!=null) {
                    System.out.println("naa gihapon ang map. puta");
                }*/
                transaction.addToBackStack(null);
                // Commit the transaction
                transaction.commit();
            }

        } else if (id == R.id.nav_slideshow) { //Videos
            System.out.println("Videos clicked");
            fragment = (VideoFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_video);

                fragment = new VideoFragment();
                transaction.replace(R.id.layout_content, fragment);
                transaction.addToBackStack(null);
                // Commit the transaction
                transaction.commit();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
