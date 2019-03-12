package com.example.texttraq;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import androidx.room.Room;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkFirstRun();
    }

    Intent intent = new Intent(this, activity_contacts.class);

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

            //fairly sure this is incorrect
            AppDataBase db = Room.databaseBuilder(getApplicationContext(), AppDataBase.class, "database-name").build();
            defaultDB = ((AppDataBase) db).defaultDao();
            DefaultSettings defaultSettings = new DefaultSettings(1,15,false, false, false,"This is an automated message");
            DefaultSettingsDao.createDefaultSettings(defaultSettings);
            //fairly sure this is incorrect, ignore for now
        }
    }
}


