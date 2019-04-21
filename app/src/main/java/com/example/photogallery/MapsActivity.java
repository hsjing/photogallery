package com.example.photogallery;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.widget.SearchView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    OkHttpClient client = new OkHttpClient();
    private float lat, lon;
    private String key, apiReq;
    private GoogleMap mMap;
    private LatLng loc;

    private SearchManager searchManager;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        key = "AIzaSyAYSwy3M_BgUJ5106kJNDJEbQymxLjaObM";

        // Obtain the SupportMapFragment and get notified when the map is ready to
        // be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(
                        R.id.map);
        mapFragment.getMapAsync(this);

        // Get searchview
        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) findViewById(R.id.maps_Search);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchLocation(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        lat = getIntent().getExtras().getFloat("lat");
        lon = getIntent().getExtras().getFloat("long");

        // Cosmetics
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setSubmitButtonEnabled(true);
        searchView.setBackgroundColor(Color.WHITE);
        getSupportActionBar().setBackgroundDrawable( new ColorDrawable(Color.parseColor("#3F51B5")));

    }

    private void searchLocation(String query) {
        apiReq = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lon + "&radius=1000&type=" + query + "&key=AIzaSyAYSwy3M_BgUJ5106kJNDJEbQymxLjaObM";
        new asyncTask().execute();
    }

    private class asyncTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {

            try {

                // Run the API
                Log.d("asyntask", run(apiReq));
                Thread.sleep(500);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String result) {
            // Parse info and load into a heap, assume every building id is unique
            // at this point, know how many unique buildings are there, so store value into N

            // clear markers
            mMap.clear();

            // new array list
            int N = 10; // N dummy value for testing
            List<Pair<Float, Float>> listOfLandmarks = new ArrayList<>(N);

            // Iterate over all members in list of lat/lng pairs, and add markers
            for (Pair<Float, Float> c : listOfLandmarks) {
                loc = new LatLng(c.first, c.second);
                mMap.addMarker(new MarkerOptions().position(loc).title(
                        "Latitude: " + lat + ", Longitude: " + lon));
            }

        }
    }

    // Fetch some shit
    private String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return "hey";
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the
     * camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be
     * prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once
     * the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(10);

        // Add a marker to the location and move the camera
        loc = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(loc).title(
                "Latitude: " + lat + ", Longitude: " + lon));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
    }
}
