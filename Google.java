package com.example.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.NinePatch;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;



public class Google extends AppCompatActivity implements LocationListener, View.OnClickListener{

    private static final int REQUEST_LOCATION = 1;
    TextView textView, txtLat;
    LocationManager locationManager;
    String lattitude,longitude;
    double latti,longi;
    private GoogleMap mMap;
    TextToSpeech tts;
    Button button;
    private MapView mMapView=null;
    EditText editText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google);

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(Google.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (Google.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Google.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        }


        button=(Button) findViewById(R.id.b1);
        button.setOnClickListener(this);


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, (LocationListener) this);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                    @Override
                    public void onMapClick(LatLng point) {

                        MarkerOptions marker= new MarkerOptions().position(new LatLng(point.latitude,point.longitude)).title("new marker");

                       mMap.clear();
                       mMap.addMarker(marker);
                       getAddressFromLocation(point.latitude,point.latitude);
                    }
                });

            }
        });


        textView = (TextView)findViewById(R.id.text_location);
        textView.setSelected(true);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i == TextToSpeech.SUCCESS){
                    int res= tts.setLanguage(new Locale("hi"));

                    if(res == TextToSpeech.LANG_MISSING_DATA|| res== TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("TTS","Language not supported");
                    }else{
                        Log.i("TTS", "Language Supported.");

                    }
                    Log.i("TTS", "Initialization success.");

                }else {
                    Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                }
            }

        });




    }
    public void onClick(View view) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getApplicationContext(), "Searching..", Toast.LENGTH_SHORT).show();

            getLocation();
            speak();
        }
    }
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_google,menu);

        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {


            case (R.id.normal):
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                Toast.makeText(getApplicationContext(), "normal", Toast.LENGTH_SHORT).show();

                return true;



            case (R.id.satellite):
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                Toast.makeText(getApplicationContext(), "satellite", Toast.LENGTH_SHORT).show();

                return true;


            case (R.id.terrain):
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                Toast.makeText(getApplicationContext(), "terrain", Toast.LENGTH_SHORT).show();

                return true;


            case (R.id.hybrid):
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                Toast.makeText(getApplicationContext(), "hybrid", Toast.LENGTH_SHORT).show();

                return true;

        }
        return true;
    }




    public void getAddressFromLocation(double latti, double longi) {
        Geocoder geocoder = new Geocoder(com.example.myapplication.Google.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latti, longi, 1);
            String address = addresses.get(0).getAddressLine(0);

            String finalAddress = address+"\n";
            textView.setText(finalAddress);
        //    Toast.makeText(getApplicationContext(), "getAddress", Toast.LENGTH_SHORT).show();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void speak(){
        String text = textView.getText().toString();
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null,null);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
    public void ProduceMarker(double latti,double longi) {

            LatLng position = new LatLng(latti, longi);
        mMap.clear();

        mMap.addMarker(new MarkerOptions().position(position)
                    .title("Marker"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        float zoomLevel = 13.0f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoomLevel));


        getAddressFromLocation(latti,longi);
    }
    @Override
    public void onLocationChanged(Location location) {
        latti = location.getLatitude();
        longi = location.getLongitude();


        LatLng latLng = new LatLng(latti, longi);
        lattitude = String.valueOf(latti);
        longitude = String.valueOf(longi);
        textView.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
        ProduceMarker(latti,longi);

    }


   public void getLocation() {
        if (ActivityCompat.checkSelfPermission(Google.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (Google.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Google.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location1 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);
            if (location != null) {
                latti = location.getLatitude();
                longi = location.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);
                  textView.setText("Your current location is"+ "\n" + "Lattitude = " + lattitude
                  + "\n" + "Longitude = " + longitude);


            } else  if (location1 != null) {
                latti = location1.getLatitude();
                longi = location1.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                 textView.setText("Your current location is"+ "\n" + "Lattitude = " + lattitude
                  + "\n" + "Longitude = " + longitude);


            } else  if (location2 != null) {
                latti = location2.getLatitude();
                longi = location2.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                   textView.setText("Your current location is"+ "\n" + "Lattitude = " + lattitude
               + "\n" + "Longitude = " + longitude);

            }else{

                Toast.makeText(this,"Unable to Trace your location",Toast.LENGTH_SHORT).show();

            }
        ProduceMarker(latti,longi);

        }
    }
    public void onMapSearch(View view) {
        EditText locationSearch = (EditText) findViewById(R.id.edittext);
        String location = locationSearch.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            if(addressList.size()>0){
                Address address = addressList.get(0);

                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                float zoomLevel = 13.0f; //This goes up to 21
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
        }
            else{
                Toast.makeText(this,"Unable to search the location",Toast.LENGTH_SHORT).show();

            }


        }
    }




    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }




    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}


