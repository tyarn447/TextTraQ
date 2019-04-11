package com.example.texttraq;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;

import com.example.texttraq.AppDataBase;
import com.example.texttraq.ContactTableDao;

import androidx.room.Room;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        final Bundle extras = intent.getExtras();
        String myLoc = (String) extras.get("address");

        SmsManager smsManager = SmsManager.getDefault();

        String aMessage = "Hey this is a text message from your app you are currently in" + myLoc;
        smsManager.sendTextMessage("2073176507",null,aMessage,null,null);
    }
}

