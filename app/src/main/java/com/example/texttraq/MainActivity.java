package com.example.texttraq;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    public int REQUESTCODE = 0;
    private MapView mapView;
    private GoogleMap gmap;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    public Location myLocation = null;
    public Location nextLocation = new Location("");
    private AlarmManager manager;
    private PendingIntent pendingIntent;
    private LocationManager locationManager = null;
    public Integer tryInt = 0;
    public Integer minutes = 0;
    AppDataBase db = null;

    private static String url;
    private String TAG = MainActivity.class.getSimpleName();
    public String eta;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @TargetApi(Build.VERSION_CODES.M)
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void run() {
            callDeleteCache();
            getCurrLocation();
            SmsManager smsManager = SmsManager.getDefault();
            String aMessage = null;
            requestLocation();
            minutes ++;
            Log.d("MINUTES",minutes.toString());

            //AppDataBase db = Room.databaseBuilder(this, AppDataBase.class, "db-data").allowMainThreadQueries().build();
            ContactTableDao contactTableDao = db.getContactDao();
            ContactTable contactTable[] = contactTableDao.getAllContacts();
            for(int i = 0; i < contactTable.length; i++) {
                ContactTable aContact = contactTable[i];
                String number = aContact.getNumber();
                int timeBetween = aContact.getTime();
                String custMsg = aContact.getCustomMessage();
                if (minutes % timeBetween == 0) {
                    //ASSERT:this is a minute where we want to text that contact
                    if (aContact.getLocation()) {
                        //ASSERT:we want to send Location for this contact
                        try {
                            custMsg = custMsg + " Location: " + getLocationName(myLocation.getLatitude(),myLocation.getLongitude());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (aContact.getSpeed()) {
                        //ASSERT:we want to send speed for this contact
                        custMsg = custMsg + " SpeedTrue";
                    }
                    if (aContact.getETA()) {
                        //ASSERT: we want to send ETA for this contact
                        custMsg = custMsg + " ETA: " + eta;
                    }
                    smsManager.sendTextMessage(number, null, custMsg, null, null);
                }
            }


            try {
                aMessage = "Hey this is a text message from your app you are currently in" + getLocationName(myLocation.getLatitude(),myLocation.getLongitude()) + " " + eta;
            } catch (IOException e) {
                e.printStackTrace();
            }
            smsManager.sendTextMessage("2073176507", null, aMessage, null, null);
            handler.postDelayed(this,1000*60);
        }
    };


    private LocationListener mListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            myLocation = location;
            Log.d("STATE", "On location Changed");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

    };


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkFirstRun();


        //create database
        db = Room.databaseBuilder(this, AppDataBase.class, "db-data").allowMainThreadQueries().build();


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
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
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //requestLocation();
        getCurrLocation();
    }


    //PRE:on create has been called
    //POST:updates location to be current location
    public void requestLocation() {
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mListener);

    }

    //PRE:none
    //POST:only here because cant use this as context outside of this class
    public void callDeleteCache(){

        deleteCache(this);
        if(nextLocation != null) {
            new GetEta().execute();
        }
    }

    //PRE:needs context of app
    //POST:deletes cache for this app, this is so it can update its location
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}


    }

    //PRE:none
    //POST:deletes cache directory recursively.
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }


    //PRE:contacts page exists
    //POST:loads the contact page
    public void goToContacts(View view) {
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
    }

    //PRE:main page exists
    //POST:loads the main page
    public void goToMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //PRE:settings page exists
    //POST:loads the settings page
    public void goToSettings(View view) {
        Intent intent = new Intent(this, settingsActivity.class);
        startActivity(intent);
    }


    //PRE:Object exists
    //POST:checks if this is the first run of the app, if it is puts default values into database
    //else does nothing
    public void checkFirstRun() {
        boolean notFirstRun = false;
        db = Room.databaseBuilder(this, AppDataBase.class, "db-data").allowMainThreadQueries().build();

        SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);
        notFirstRun = settings.getBoolean("FIRST_RUN", false);
        if (!notFirstRun) {
            //ASSERT:This is the first time we are running the app
            settings = getSharedPreferences("PREFS_NAME", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("FIRST_RUN", true);
            editor.commit();
            //Now put default values in


            //AppDataBase db = Room.databaseBuilder(this, AppDataBase.class, "db-data").allowMainThreadQueries().build();
            DefaultSettingsDao defaultDao = db.getDefaultDao();
            DefaultSettings defaultSettings = new DefaultSettings(1, 15, false, false, false, "Please Create your Custom Message");
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
        gmap.setMinZoomPreference(15);
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
        //LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationManager lm = locationManager;

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
            gmap.clear();
            gmap.addMarker(new MarkerOptions().position(latLng).title("Destination"));
        }

    }









    public void pause(View view) {
        Button startButton3 = findViewById(R.id.startButton3);
        Button startButton = findViewById(R.id.startButton);
        Button startButton2 = findViewById(R.id.startButton2);
        startButton2.setVisibility(View.INVISIBLE);
        startButton.setVisibility(View.VISIBLE);
        cancelRunnable(view);
    }

    public void stop(View view) {
        Button startButton3 = findViewById(R.id.startButton3);
        Button startButton = findViewById(R.id.startButton);
        Button startButton2 = findViewById(R.id.startButton2);
        startButton.setVisibility(View.VISIBLE);
        startButton2.setVisibility(View.INVISIBLE);
        startButton3.setVisibility(View.INVISIBLE);

        cancelRunnable(view);
    }




    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void start(View view) throws IOException {
        handler.postDelayed(runnable,1000 * 60);
        Button startButton3 = findViewById(R.id.startButton3);
        Button startButton = findViewById(R.id.startButton);
        Button startButton2 = findViewById(R.id.startButton2);

        startButton.setVisibility(View.INVISIBLE);
        startButton2.setVisibility(View.VISIBLE);
        startButton3.setVisibility(View.VISIBLE);


        //AppDataBase db = Room.databaseBuilder(this, AppDataBase.class, "db-data").allowMainThreadQueries().build();
        ContactTableDao contactTableDao = db.getContactDao();

        float distanceInMeters = myLocation.distanceTo(nextLocation);

        //use google maps api instead to make a request using two lats and longs
        //thatll get you the ETA
        getCurrLocation();

        new GetEta().execute();


    }

    public void cancelRunnable(View view) {
        handler.removeCallbacks(runnable);
        minutes = 0;
    }






    //Alex Code
    private class GetEta extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=";
            url += myLocation.getLatitude();
            url += ",";
            url += myLocation.getLongitude();
            url += "&destinations=";
            url += nextLocation.getLatitude();
            url += ",";
            url += nextLocation.getLongitude();
            url += "&key=AIzaSyDwJG7PNHMH7-Qu7HJzRrkU1-9zJat2XMw";

            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray rows = jsonObj.getJSONArray("rows");

                    JSONObject c = rows.getJSONObject(0);

                    JSONArray elements = c.getJSONArray("elements");
                    JSONObject v = elements.getJSONObject(0);
                    JSONObject duration = v.getJSONObject("duration");
                    eta = duration.getString("text");
                    Log.e("TAG", "Value: " + eta);

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }

            return null;
        }

    }



}


