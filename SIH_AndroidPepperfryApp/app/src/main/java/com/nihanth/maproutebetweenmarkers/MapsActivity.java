package com.nihanth.maproutebetweenmarkers;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journaldev.maproutebetweenmarkers.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    //ArrayList markerPoints= new ArrayList();
    private MarkerOptions options = new MarkerOptions();
    private ArrayList<LatLng> latlngs = new ArrayList<>();
    public ArrayList<String> strings = new ArrayList<>();
    public ArrayList<Double> strings_lat = new ArrayList<>();
    public ArrayList<Double> strings_long = new ArrayList<>();
    public ArrayList<String> stringArrayList = new ArrayList<>();
    Button help_button, order_summary_button;
    List<String> cust_names = new ArrayList<>();
    List<String> cust_address = new ArrayList<>();
    List<String> cust_ids = new ArrayList<>();
    int size = 0;
    FloatingActionButton floatingActionButton;
    String did = "";
    GPSTracker gpsTracker;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    ArrayList<String> x = new ArrayList<>();
    //public ArrayList<String> cust_details = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        final Intent intent = getIntent();
        if (intent != null) {
            strings = intent.getStringArrayListExtra("coord");
            stringArrayList = intent.getStringArrayListExtra("cust");
            did = intent.getStringExtra("did");
            cust_ids = intent.getStringArrayListExtra("ids");
            Log.d("hyu", stringArrayList.get(0));
            for (String i : strings) {
                String[] x = i.trim().split(" ");
                //StringBuilder builder = new StringBuilder(x[0]);
                strings_lat.add(Double.parseDouble(x[0]));
                strings_long.add(Double.parseDouble(x[1]));
            }
            for (String v : stringArrayList) {
                String[] u = v.split("::");
                Log.d("huo", u[0]);
                cust_names.add(u[0]);
                cust_address.add(u[1]);
                size++;
            }
            for (String w : cust_ids){
                x.add(w);

            }
            floatingActionButton = findViewById(R.id.fab);
            //size=stringArrayList.size();
            //Log.d("uiop",""+size);


        }

        //gpsTracker = new GPSTracker(MapsActivity.this);
        /*if (gpsTracker.canGetLocation()){
            Double lat = gpsTracker.getLatitude();
            Double longi = gpsTracker.getLongitude();
            Log.d("lat",""+lat);
            Log.d("long",""+longi);
        }*/

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5 * 1000)
                .setFastestInterval(60 * 1000);
        mGoogleApiClient.connect();

        final int sizee = stringArrayList.size()-1;  //-1 initially
        Log.d("debbb",""+sizee);

        help_button = findViewById(R.id.manager);
        order_summary_button = findViewById(R.id.order_summary);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        help_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(MapsActivity.this, ManagerHelp.class);
                startActivity(intent1);
            }
        });
        order_summary_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ui", "" + sizee);
                Intent intent1 = new Intent(MapsActivity.this, OrderSummary.class);
                intent1.putExtra("size", sizee);
                intent1.putExtra("did", did);
                intent1.putStringArrayListExtra("ids",x);
                intent1.putStringArrayListExtra("cust", (ArrayList<String>) cust_address);
                intent1.putStringArrayListExtra("cust_name", (ArrayList<String>) cust_names);
                startActivity(intent1);
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(Intent.ACTION_SENDTO);
                intent1.setType("message/rfc822");
                String[] strings = new String[1];
                strings[0]="nihanth876@gmail.com";
                intent1.setData(Uri.parse("mailto:"));
                intent1.putExtra(Intent.EXTRA_EMAIL,strings);
                intent1.putExtra(Intent.EXTRA_SUBJECT, "PROBLEM RELATED TO DELIVERY");
                if (intent1.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent1);

                }
            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //LatLng sydney = new LatLng(17.373567,  78.292635);
        //latlngs.add(sydney);
        //latlngs.add(new LatLng(17.390554, 78.356180));
        //latlngs.add(new LatLng(17.4006, 78.32333));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));
        //String url = getDirectionsUrl(latlngs.get(0), latlngs.get(1));
        //String url_mod = getDirectionsUrl(latlngs.get(1), latlngs.get(2));
        for (int i = 0; i < strings_lat.size(); i++) {
            LatLng y = new LatLng(strings_lat.get(i), strings_long.get(i));
            latlngs.add(y);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlngs.get(0), 12));
        String[] urls = new String[latlngs.size() - 1];
        for (int i = 0; i < urls.length; i++) {
            urls[i] = getDirectionsUrl(latlngs.get(i), latlngs.get(i + 1)) + "&key=AIzaSyCLYpdfkqJlpaVgUrkzulzYr3uvspxd3wU";
        }
        // url=url+"&key=AIzaSyBK6C2_vlhw2pMEBrUdCfFsZ74HHB652cs";
        // url_mod=url_mod+"&key=AIzaSyBK6C2_vlhw2pMEBrUdCfFsZ74HHB652cs";
        //Log.d("yo  ", urls[0]);
        for (LatLng point : latlngs) {
            int numm = latlngs.indexOf(point);
            Log.d("nummm",""+numm);
            //if (numm > 3) {
            //    numm = numm % 3;
            //}
            String num = "number" + (numm);
            int resId = getResources().getIdentifier(
                    num,
                    "drawable",
                    getPackageName());
            Bitmap x = BitmapFactory.decodeResource(getResources(), resId);
            //
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            x.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            x.recycle();
            Bitmap b = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            //profileImage.setImageBitmap(Bitmap.createScaledBitmap(b, 120, 120, false));
            options.position(point);
            if (numm==0){
                options.title("");
                options.snippet(" ");
            }
            else {
                options.title(cust_names.get(numm - 1)); //numm
                options.snippet(cust_address.get(numm-1)); //numm
            }
            options.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(b, 90, 120, false)));
            mMap.addMarker(options);
        }
        // DownloadTask downloadTask = new DownloadTask();
        //DownloadTask downloadTask1 = new DownloadTask();
        // Start downloading json data from Google Directions API
        //downloadTask.execute(url);
        //downloadTask1.execute(url_mod);


        for (int i = 0; i < urls.length; i++) {
            DownloadTask dt = new DownloadTask();
            dt.execute(urls[i]);
        }

        //mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
        //    @Override
        /*    public void onMapClick(LatLng latLng) {

                if (markerPoints.size() > 1) {
                    markerPoints.clear();
                    mMap.clear();
                }

                // Adding new item to the ArrayList
                markerPoints.add(latLng);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(latLng);

                if (markerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (markerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);

                // Checks, whether start and end locations are captured
                if (markerPoints.size() >= 2) {
                    LatLng origin = (LatLng) markerPoints.get(0);
                    LatLng dest = (LatLng) markerPoints.get(1);

                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, dest);

                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                }

            }
        });*/

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location!=null){
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);

        }
        else{
            final Double currentLatitude = location.getLatitude();
            final Double currentLongitude = location.getLongitude();
            Log.d("latlong",currentLatitude+" "+currentLongitude);

            final FirebaseDatabase database = FirebaseDatabase.getInstance();

            DatabaseReference myRef = database.getReference().child("users");
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot item_snapshot:dataSnapshot.getChildren()) {
                        //Log.d("heyyy", item_snapshot.child("Coordinates").getValue().toString());
                        if (item_snapshot.child("driverid").getValue().toString().equals(did)) {
                            item_snapshot.child("Coordinates").getRef().push().setValue(currentLatitude + " " + currentLongitude);
                            Log.d("succededd","succeded");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("Gonee","gone");
                }
            });
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onLocationChanged(Location location) {
        final Double currentLatitude = location.getLatitude();
        final Double currentLongitude = location.getLongitude();
        Log.d("Latlon",currentLatitude+" "+currentLongitude);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference().child("users");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot item_snapshot:dataSnapshot.getChildren()) {
                    //Log.d("heyyy", item_snapshot.child("Coordinates").getValue().toString());
                    if (item_snapshot.child("driverid").equals(did)) {
                        item_snapshot.child("Coordinates").getRef().setValue(currentLatitude + " " + currentLongitude);
                        Log.d("succeded","succeded");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Gonee","gone");
            }
        });
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }


    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("routes",""+routes);
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                Log.d("enter","entered");
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    Log.d("debugg",""+position);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }


            mMap.addPolyline(lineOptions);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;


        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        String sensor = "sensor=false";
        String mode = "mode=driving";

        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;


        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
            mGoogleApiClient.disconnect();
        }
        else {
            mGoogleApiClient.connect();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }
}
