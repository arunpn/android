package com.mauriciogiordano.travell;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mauriciogiordano.travell.helper.DirectionsJSONParser;
import com.mauriciogiordano.travell.model.Destination;
import com.mauriciogiordano.travell.model.Place;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Mauricio Giordano on 11/7/15.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Travell, 2015 - All rights reserved.
 */
public class ItineraryActivity extends ActionBarActivity {

    private SupportMapFragment mapF;
    private GoogleMap map;
    private ArrayList<LatLng> markerPoints;
    public Destination destination;
    public List<Place> placeList;

    private Map<String, Bitmap> bitmapMap = new HashMap<>();

    private class LoadImages extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            for (Place place : placeList) {
                try {
                    FutureTarget<Bitmap> future = Glide.with(ItineraryActivity.this)
                            .load(place.getImages().get(0))
                            .asBitmap()
                            .into(300, 300);

                    bitmapMap.put(place.getId(), future.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);

        String destinationId = getIntent().getStringExtra("destinationId");

        if (destinationId == null || destinationId.equals("")) {
            finish();
            return;
        }

        destination = Destination.find(destinationId, this);

        if (destination == null) {
            finish();
            return;
        }

        placeList = Place.findForDestination(destination.getId(), this);

        new LoadImages().execute();

        // Initializing
        markerPoints = new ArrayList<LatLng>();

        FragmentManager fm = getSupportFragmentManager();
        mapF = (SupportMapFragment) fm.findFragmentById(R.id.map);

        if (mapF == null) {
            mapF = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, mapF).commit();
        }

        getSupportActionBar().setTitle("Itinerary");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (map == null) {
            map = mapF.getMap();

            if (map != null) {

                // Enable MyLocation Button in the Map
                map.setMyLocationEnabled(true);

                map.getUiSettings().setZoomControlsEnabled(true);
                map.getUiSettings().setZoomGesturesEnabled(true);

                for (Place place : placeList) {
                    LatLng latLng = new LatLng(Double.parseDouble(place.getLatitude()),
                            Double.parseDouble(place.getLongitude()));

                    markerPoints.add(latLng);

                    // Creating MarkerOptions
                    MarkerOptions options = new MarkerOptions();

                    // Setting the position of the marker
                    options.position(latLng);

                    /**
                     * For the start location, the color of marker is GREEN and
                     * for the end location, the color of marker is RED and
                     * for the rest of markers, the color is AZURE
                     */
                    float[] hsv = new float[3];
                    int[] rgb = new int[3];
                    rgb[0] = Color.red(place.getColor());
                    rgb[1] = Color.green(place.getColor());
                    rgb[2] = Color.blue(place.getColor());

                    Color.RGBToHSV(rgb[0], rgb[1], rgb[2], hsv);
                    options.icon(BitmapDescriptorFactory.defaultMarker(hsv[0]));
                    options.title(place.getName());

                    options.snippet("" + placeList.indexOf(place));

                    // Add new marker to the Google Map Android API V2
                    map.addMarker(options);
                }

                map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {

                        View layout = getLayoutInflater().inflate(R.layout.adapter_map_balloon, null);

                        TextView title = (TextView) layout.findViewById(R.id.title);
                        title.setText(marker.getTitle());

                        ImageView snippet = (ImageView) layout.findViewById(R.id.snippet);

                        int pos = Integer.parseInt(marker.getSnippet());
                        Place place = placeList.get(pos);

                        if (bitmapMap.containsKey(place.getId())) {
                            snippet.setImageBitmap(bitmapMap.get(place.getId()));
                        } else {
                            try {
                                Glide.with(ItineraryActivity.this)
                                        .load(place.getImages().get(0))
                                    .asBitmap()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .centerCrop()
                                    .into(snippet);
                            } catch (Exception e) {}
                        }

                        return layout;
                    }
                });

                Place firstPlace = placeList.get(0);

                map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(Double.parseDouble(firstPlace.getLatitude()),
                                Double.parseDouble(firstPlace.getLongitude())), 12.0f));

                LatLng origin = markerPoints.get(0);
                LatLng dest = markerPoints.get(1);

                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(origin, dest);

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
            }
        }
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Waypoints
        String waypoints = "";
        for(int i=2;i<markerPoints.size();i++){
            LatLng point  = (LatLng) markerPoints.get(i);
            if(i==2)
                waypoints = "waypoints=";
            waypoints += point.latitude + "," + point.longitude + "|";
        }

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+waypoints;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while..", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service

            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("destinationId", destination.getId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_itinerary, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Intent i = new Intent(this, DestinationActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();

            return true;
        } else if (item.getItemId() == R.id.action_details) {
            Intent i = new Intent(this, ItineraryDescriptionActivity.class);
            i.putExtra("destinationId", destination.getId());
            startActivity(i);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
