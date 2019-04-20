package com.example.texttraq;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;


import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import androidx.annotation.RequiresApi;
import androidx.room.Room;
import androidx.room.RoomDatabase;

public class AlarmReceiver extends BroadcastReceiver {

    public Location myLocation = null;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {

        final Bundle extras = intent.getExtras();
        String myLoc = (String) extras.get("address");
        //Bundle b = intent.getExtras();
        //String[] contacts =  b.getStringArray("allContacts");
        //String myLoc = "";
        MainActivity helper = new MainActivity();
       //String contacts = (String) extras.get("contactNames");
        ArrayList<String> contactNames = (ArrayList<String>) extras.getSerializable("contactNames");
        ArrayList<Integer> timeBetween = (ArrayList<Integer>) extras.getSerializable("timeBetween");
        ArrayList<Boolean> location = (ArrayList<Boolean>) extras.getSerializable("location");


        //String contactNames[] = contacts.split("|");
        for(String aString: contactNames){
            Log.d("NAME",aString);
        }
        for(Integer aInt: timeBetween){
            Log.d("TIME",aInt.toString());
        }
        for(Boolean aBool: location){
            if(aBool){
                Log.d("BOOL","true");
            }
            else{
                Log.d("BOOL","false");
            }
        }

        SmsManager smsManager = SmsManager.getDefault();
        String aMessage = "Hey this is a text message from your app you are currently in" + myLoc;
        smsManager.sendTextMessage("2073176507", null, aMessage, null, null);

    }

}

