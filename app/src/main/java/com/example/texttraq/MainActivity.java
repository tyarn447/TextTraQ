package com.example.texttraq;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private GoogleMap gmap;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkFirstRun();

        Bundle mapViewBundle = null;
        if(savedInstanceState != null){
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);



    }

    public void goToContacts(View view) {
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
    }

    public void goToMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(this, settingsActivity.class);
        startActivity(intent);
    }




    //PRE:Object exists
    //POST:checks if this is the first run of the app, if it is puts default values into database
    //else does nothing
    public void checkFirstRun(){
        boolean notFirstRun = false;

        SharedPreferences settings = getSharedPreferences("PREFS_NAME",0);
        notFirstRun = settings.getBoolean("FIRST_RUN",false);
        if(!notFirstRun){
            //ASSERT:This is the first time we are running the app
            settings = getSharedPreferences("PREFS_NAME",0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("FIRST_RUN",true);
            editor.commit();
            //Now put default values in


            AppDataBase db = Room.databaseBuilder(this, AppDataBase.class, "db-data").allowMainThreadQueries().build();
            DefaultSettingsDao defaultDao = db.getDefaultDao();
            DefaultSettings defaultSettings = new DefaultSettings(1,15,false,false,false,"This is an automated message");
            defaultDao.insert(defaultSettings);
            //This should have created an entry in the database for default settings

            DefaultSettings newDefaultSettings = defaultDao.getDefaultSettings();
            TextView tv = (TextView)findViewById(R.id.textView2);
            tv.setText("Welcome to TextTraQ, please go to the Settings Page to initialize your preferred settings for all contacts that will be added!");



        }
    }

    //********************
    //all overrides just implemented for mapviews sake
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    //*******************************

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(12);
        LatLng ny = new LatLng(40.7143528, -74.0059731);
        gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));
    }

    //PRE:edittext and button need to exist
    //POST:finds addresses close to the one that you put in
    public void onSearchPress(View view){
        EditText search = (EditText) findViewById(R.id.editText);
        String location = search.getText().toString();
        List<Address> addressList = null;

        if(location != null || !location.equals("")){
            Geocoder geocoder = new Geocoder(this);
            try{
                addressList = geocoder.getFromLocationName(location,1);
            }
            catch(IOException e){
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            gmap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            TextView tv = (TextView)findViewById(R.id.textView2);
            tv.setText("If the map currently shows your desired destination and you would like to start your journey, please press the 'Start Journey' button, if it is not, please enter a more specific address.");

        }
    }







    public void pause(View view) {
        Button startButton3 = findViewById(R.id.startButton3);
        Button startButton = findViewById(R.id.startButton);
        Button startButton2 = findViewById(R.id.startButton2);
        startButton2.setVisibility(View.INVISIBLE);
        startButton.setVisibility(View.VISIBLE);
    }

    public void stop(View view) {
        Button startButton3 = findViewById(R.id.startButton3);
        Button startButton = findViewById(R.id.startButton);
        Button startButton2 = findViewById(R.id.startButton2);
        startButton.setVisibility(View.VISIBLE);
        startButton2.setVisibility(View.INVISIBLE);
        startButton3.setVisibility(View.INVISIBLE);
    }

    public void start(View view) {
        Button startButton3 = findViewById(R.id.startButton3);
        Button startButton = findViewById(R.id.startButton);
        Button startButton2 = findViewById(R.id.startButton2);

        startButton.setVisibility(View.INVISIBLE);
        startButton2.setVisibility(View.VISIBLE);
        startButton3.setVisibility(View.VISIBLE);
    }


}


