package com.example.texttraq;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkFirstRun();


    }

    Intent intent = new Intent(this, ContactsActivity.class);

    public void goToContacts(View view) {
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
            tv.setText("Welcome to TextTraQ, please go to the Settings Page to initialze your preferred settings for all contacts that will be added!");



        }
    }
}


