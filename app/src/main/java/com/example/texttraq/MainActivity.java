package com.example.texttraq;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
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
import java.util.Locale;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private GoogleMap gmap;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    public Location myLocation = null;
    public Location nextLocation = new Location("");
    private AlarmManager manager;
    private PendingIntent pendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkFirstRun();

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
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
    public void checkFirstRun() {
        boolean notFirstRun = false;

        SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);
        notFirstRun = settings.getBoolean("FIRST_RUN", false);
        if (!notFirstRun) {
            //ASSERT:This is the first time we are running the app
            settings = getSharedPreferences("PREFS_NAME", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("FIRST_RUN", true);
            editor.commit();
            //Now put default values in


            AppDataBase db = Room.databaseBuilder(this, AppDataBase.class, "db-data").allowMainThreadQueries().build();
            DefaultSettingsDao defaultDao = db.getDefaultDao();
            DefaultSettings defaultSettings = new DefaultSettings(1, 15, false, false, false, "This is an automated message");
            defaultDao.insert(defaultSettings);
            //This should have created an entry in the database for default settings

            DefaultSettings newDefaultSettings = defaultDao.getDefaultSettings();
            TextView tv = (TextView) findViewById(R.id.textView2);
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(12);
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        myLocation = location;
        //now we have our current location
        LatLng myLoc = new LatLng(latitude, longitude);
        gmap.moveCamera(CameraUpdateFactory.newLatLng(myLoc));
    }

    //PRE:permissions to access location are granted
    //POST:gets last known/current location and sets our class variable lcation
    //to be that location
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getCurrLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        myLocation = location;
    }

    //PRE:lat and long are the lattitude and longitude of the location you want the name of
    //POST:returns city and state name of the place you are currently
    public String getLocationName(double lat, double longitude) throws IOException {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(lat, longitude, 1);
        String cityName = addresses.get(0).getAddressLine(0);
        String stateName = addresses.get(0).getAddressLine(1);
        String retVal = cityName;
        return retVal;
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
            nextLocation.setLatitude(address.getLatitude());
            nextLocation.setLongitude(address.getLongitude());
            tv.setText("If the map currently shows your desired destination and you would like to start your journey, please press the 'Start Journey' button, if it is not, please enter a more specific address.");

        }
    }







    public void pause(View view) {
        Button startButton3 = findViewById(R.id.startButton3);
        Button startButton = findViewById(R.id.startButton);
        Button startButton2 = findViewById(R.id.startButton2);
        startButton2.setVisibility(View.INVISIBLE);
        startButton.setVisibility(View.VISIBLE);
        cancelAlarm(view);
    }

    public void stop(View view) {
        Button startButton3 = findViewById(R.id.startButton3);
        Button startButton = findViewById(R.id.startButton);
        Button startButton2 = findViewById(R.id.startButton2);
        startButton.setVisibility(View.VISIBLE);
        startButton2.setVisibility(View.INVISIBLE);
        startButton3.setVisibility(View.INVISIBLE);

        cancelAlarm(view);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void start(View view) throws IOException {
        Button startButton3 = findViewById(R.id.startButton3);
        Button startButton = findViewById(R.id.startButton);
        Button startButton2 = findViewById(R.id.startButton2);

        startButton.setVisibility(View.INVISIBLE);
        startButton2.setVisibility(View.VISIBLE);
        startButton3.setVisibility(View.VISIBLE);


        float distanceInMeters = myLocation.distanceTo(nextLocation);

        //use google maps api instead to make a request using two lats and longs
        //thatll get you the ETA


        SmsManager smsManager = SmsManager.getDefault();
        getCurrLocation();
        String myLoc = getLocationName(myLocation.getLatitude(),myLocation.getLongitude());
        // Retrieve a PendingIntent that will perform a broadcast
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        Bundle aBundle = new Bundle();
        aBundle.putString("address",myLoc);
        alarmIntent.putExtras(aBundle);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        int interval = 1000 * 5;

        manager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),interval,pendingIntent);
        


    }

    public void cancelAlarm(View view) {
        if (manager != null) {
            manager.cancel(pendingIntent);
        }
    }


}


