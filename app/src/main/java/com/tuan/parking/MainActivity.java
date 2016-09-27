package com.tuan.parking;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.tuan.parking.Adapter.ViewPagerAdapter;
import com.tuan.parking.Model.Parking;
import com.tuan.parking.slidingtab.SlidingTabLayout;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static List<Parking> parkingList;

    private ViewPager pager;
    private SlidingTabLayout tabs;
    private ViewPagerAdapter adapter;
    CharSequence Titles[]={"Map","List"};
    int Numboftabs =2;

    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private static final String TAG = "MainActivity";
    private static final String LOG_TAG = "PlacesAPIActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private GoogleApiClient mGoogleApiClient;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private double lat, lng;
    private LatLng latLng;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs);

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        tabs.setViewPager(pager);
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
        switch (id){

            case R.id.action_search:
                try {
                    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                            .build();
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .setFilter(typeFilter)
                                    .build(this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.d("MainActivity", e.toString());
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.d("MainActivity", e.toString());
                }

                return true;
            case R.id.action_current_place:
                getCurrentLocation();

                Toast.makeText(this, "lat: "+lat + "\n"+"long: "+lng, Toast.LENGTH_LONG).show();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void getCurrentLocation() {
        if(checkPermission()) {
            LocationManager locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);
            final LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Log.i(TAG, "location status changed");
                }

                @Override
                public void onProviderEnabled(String provider) {
                    Log.i(TAG, "location provider enabled");
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Log.i(TAG, "location provider disabled");
                }
            };
            // Check whether if the GPS provider exists and if it is enabled in the device
            // If the GPS is enabled, request location updates.
            if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)
                    && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // request location update with the location listener to use onLocationChanged(),
                // given the name of the provider, minimum time interval as 5 seconds,
                // and minimum distance as 10 meters between location updates
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        5000, 10, locationListener);
            }
            lat = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
            lng = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
            int index = pager.getCurrentItem();
            ViewPagerAdapter adapter = (ViewPagerAdapter) pager.getAdapter();
            com.tuan.parking.FragmentTabs.MapFragment currentMapFragment
                    = (com.tuan.parking.FragmentTabs.MapFragment) adapter.getMapFragment(index);
            currentMapFragment.setLat(lat);
            currentMapFragment.setLng(lng);
            getSupportFragmentManager()
                    .beginTransaction()
                    .detach(currentMapFragment)
                    .attach(currentMapFragment)
                    .commit();
        }
    }

    private boolean checkPermission() {
        int gpsPermission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (gpsPermission == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "permissions granted");
            return true;
        } else {
            Log.i(TAG, "Device location access permission was denied");
            String[] permission = {android.Manifest.permission.ACCESS_FINE_LOCATION};
            // request for device location permission with a result id code as 0
            ActivityCompat.requestPermissions(this, permission, 0);
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            Log.i(TAG, data+ "");
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                latLng = place.getLatLng();
                //set title for toolbar
                if (!place.getName().toString().equals("")) {
                    toolbar.setTitle(place.getName().toString());
                }

                lat = latLng.latitude;
                lng = latLng.longitude;

                int index = pager.getCurrentItem();
                ViewPagerAdapter adapter = (ViewPagerAdapter) pager.getAdapter();
                com.tuan.parking.FragmentTabs.MapFragment currentMapFragment
                        = (com.tuan.parking.FragmentTabs.MapFragment) adapter.getMapFragment(index);
                currentMapFragment.setLat(lat);
                currentMapFragment.setLng(lng);
                getSupportFragmentManager()
                        .beginTransaction()
                        .detach(currentMapFragment)
                        .attach(currentMapFragment)
                        .commit();
//                Fragment fragment = getSupportFragmentManager()
//                        .findFragmentById(R.id.fragment_map_container);
//                if (fragment instanceof MapFragment){
//                    Log.i(TAG, "instanceof mapfragment is running");
//                    ((MapFragment) getSupportFragmentManager()
//                            .findFragmentById(R.id.fragment_map_container)).setLat(lat);
//                    ((MapFragment) getSupportFragmentManager()
//                            .findFragmentById(R.id.fragment_map_container)).setLng(lng);
//                    getSupportFragmentManager()
//                            .findFragmentById(R.id.fragment_map_container).onResume();
//                }

//                Toast.makeText(this, "lat: "+lat + "\n"+"lng: "+
//                        lng, Toast.LENGTH_LONG).show();

                //list view

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i("MainActivity", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                Log.i("MainActivity", "Canceled");
            }
        }
    }

}
