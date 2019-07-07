package com.example.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.geometry.Point;


import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_LOCATION = 1;
    Button button;
    TextView textView;
    LocationManager locationManager;
    String lattitude,longitude;
    double latti,longi;
    private MapView mMapView=null;
    TextToSpeech tts;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);




        textView = (TextView)findViewById(R.id.text_location);
        button = (Button)findViewById(R.id.button_location);
        mMapView = findViewById(R.id.mapView);


        button.setOnClickListener(this);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i == TextToSpeech.SUCCESS){
                    int res= tts.setLanguage(Locale.ENGLISH);

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
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);

        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {


            case (R.id.World_Street_Map):
                Basemap.Type basemapType1 = Basemap.Type.STREETS_VECTOR;
                int levelOfDetail = 17;
                ArcGISMap map;
                map = new ArcGISMap(basemapType1, latti, longi, levelOfDetail);
                mMapView.setMap(map);
                return true;


            case (R.id.World_Topo):
                Basemap.Type basemapType2 = Basemap.Type.TOPOGRAPHIC;
                levelOfDetail = 17;
                map = new ArcGISMap(basemapType2, latti, longi, levelOfDetail);
                mMapView.setMap(map);
                return true;


            case (R.id.Imagery):
                Basemap.Type basemapType4 = Basemap.Type.IMAGERY_WITH_LABELS;
                levelOfDetail = 17;
                map = new ArcGISMap(basemapType4, latti, longi, levelOfDetail);
                mMapView.setMap(map);
                return true;


            case (R.id.Gray):

                Basemap.Type basemapType3 = Basemap.Type.OCEANS;
                levelOfDetail = 17;
                map = new ArcGISMap(basemapType3, latti, longi, levelOfDetail);
                mMapView.setMap(map);
                return true;

        }
        return true;
    }




    public void getAddressFromLocation(double latti, double longi) {
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latti, longi, 1);
            String address = addresses.get(0).getAddressLine(0);

            String finalAddress = address+"\n";
            textView.setText(finalAddress);
            speak();
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


    @Override
    public void onClick(View view) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
        }
    }

    public void ProduceMarker(double latti,double longi) {

        if (mMapView != null) {
             Basemap.Type basemapType=Basemap.Type.TOPOGRAPHIC;
            int levelOfDetail= 17;
            ArcGISMap map;
            map = new ArcGISMap(basemapType,latti, longi, levelOfDetail);

            mMapView.setMap(map);

        }

        getAddressFromLocation(latti,longi);
    }





       /*Point point = new Point(28, 77, SpatialReferences.getWebMercator());
       Viewpoint vp = new Viewpoint(point, 7500);
       map.setInitialViewpoint(vp);

        // set initial map extent*/


        //  create a new graphics overlay and add it to the mapview
       /*GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
       mMapView.getGraphicsOverlays().add(graphicsOverlay);


        //create a simple marker symbol
        SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED,7);


        //add a new graphic with a new point geometry
       /*Point graphicPoint = new Point(28, 77, SpatialReferences.getWebMercator());
      Graphic graphic = new Graphic(graphicPoint, symbol);
      graphicsOverlay.getGraphics().add(graphic);*/





    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location1 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);
            if (location != null) {
                latti = location.getLatitude();
                longi = location.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);
              //  textView.setText("Your current location is"+ "\n" + "Lattitude = " + lattitude
                      //  + "\n" + "Longitude = " + longitude);


            } else  if (location1 != null) {
                latti = location1.getLatitude();
                longi = location1.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

               // textView.setText("Your current location is"+ "\n" + "Lattitude = " + lattitude
                      //  + "\n" + "Longitude = " + longitude);


            } else  if (location2 != null) {
                latti = location2.getLatitude();
                longi = location2.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

             //   textView.setText("Your current location is"+ "\n" + "Lattitude = " + lattitude
                        //+ "\n" + "Longitude = " + longitude);

            }else{

                Toast.makeText(this,"Unable to Trace your location",Toast.LENGTH_SHORT).show();

            }

            ProduceMarker(latti,longi);


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



}

