package com.tuan.parking.FragmentTabs;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.tuan.parking.JSON.JSONParser;
import com.tuan.parking.JSON.JsonLocationUrl;
import com.tuan.parking.MainActivity;
import com.tuan.parking.Model.Parking;
import com.tuan.parking.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "MapFragment";
    private MapView mapView;
    private GoogleMap map;
    private List<Marker> markerArray;
    private double lat, lng;
    private List<Parking> parkingList;

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public MapFragment() {

    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView) v.findViewById(R.id.mapview);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this); //this is important
        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        new SearchTask().execute();
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    private class SearchTask extends AsyncTask<String, String, List<Parking>> {

        /**
         * Preparing to execute task.
         */
        @Override
        protected void onPreExecute() {
//            Log.i(TAG, "Starting task");
//            mProgressBar.setVisibility(View.VISIBLE);
        }

        /**
         * Use the connection to make a http request to the
         * web service.
         * @param params a String URL for making the service request.
         * @return a list of possible nearby location matching the
         * user's request inputs. Null if no matching content.
         */
        @Override
        protected List<Parking> doInBackground(String... params) {
            JsonLocationUrl myConnection = new JsonLocationUrl();
            String content;

            Log.i(TAG, "running doinBackground");
            try {
                content = myConnection.getJson(lat, lng);
                if (content != null) {
                    // Get a list of places by parsing the JSON
                    // text content.
                    Log.i(TAG, content);
                    List<Parking> parseParkingList = new JSONParser(content).getParkingList();
                    return parseParkingList;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Parking> parkings) {

            if (parkings != null) {
                addMarkers(parkings);
                Log.i(TAG, "Parking list size on post : " + parkings.size());
            } else {
                LatLng position = new LatLng(lat, lng);
                map.addMarker(new MarkerOptions().position(position).title("I'm here"));
                map.getUiSettings().setZoomControlsEnabled(true);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
                Log.i(TAG, "Parking list on post is null");
            }
            Log.i(TAG, "task finished");
        }

        private void addMarkers(List<Parking> parkings) {
            parkingList = parkings;
            //pass parking list to main activity
            MainActivity.parkingList = parkings;

            Log.i(TAG, "MainActivity parking list size : " + MainActivity.parkingList.size());
            Log.i(TAG, "my lat: "+lat + " my lng: "+ lng);

            markerArray = new ArrayList<Marker>();
            LatLng position = new LatLng(lat, lng);
            map.addMarker(new MarkerOptions().position(position).title("I'm here"));
            //add bubleIcon markers with price for each parking
            IconGenerator icon = new IconGenerator(getActivity());
            icon.setColor(R.color.colorPrimary);
            icon.setTextAppearance(R.style.iconGenText);
            for (int i=0; i < parkingList.size(); i++) {
                position = new LatLng(parkingList.get(i).getGotLat(),
                        parkingList.get(i).getGotLng());
                double price = parkingList.get(i).getPrice();
                String name = parkingList.get(i).getName();
                map.addMarker(new MarkerOptions().position(position).title(name)
                .icon(BitmapDescriptorFactory.fromBitmap(icon.makeIcon("$"+price)))
                .anchor(icon.getAnchorU(), icon.getAnchorV()));
            }

            map.getUiSettings().setZoomControlsEnabled(true);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
        }
    }

}
