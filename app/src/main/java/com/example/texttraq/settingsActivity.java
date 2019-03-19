package com.example.texttraq;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class settingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Intent intent = getIntent();

        //set up database and get default settings
        AppDataBase db = Room.databaseBuilder(this, AppDataBase.class, "db-data").allowMainThreadQueries().build();
        DefaultSettingsDao defaultDao = db.getDefaultDao();
        DefaultSettings defaultSettings = defaultDao.getDefaultSettings();
        String custMessage = defaultSettings.getDefaultCustomMessage();
        int defaultTime = defaultSettings.getDefaultTime();
        Boolean defaultLocation = defaultSettings.getDefaultLocation();
        Boolean defaultSpeed = defaultSettings.getDefaultSpeed();
        Boolean defaultETA = defaultSettings.getDefaultETA();

        //get all textviews and switches
        TextView defTime = (TextView)findViewById(R.id.timeText);
        TextView custMsg = (TextView)findViewById(R.id.custMessage);
        Switch defLocation = (Switch)findViewById(R.id.locationSwitch);
        Switch defSpeed = (Switch)findViewById(R.id.speedSwitch);
        Switch defETA = (Switch)findViewById(R.id.ETASwitch);

        //Set all textviews and switches to the correct values
        defTime.setText(String.valueOf(defaultTime));
        custMsg.setText(custMessage);
        defLocation.setChecked(defaultLocation);
        defSpeed.setChecked(defaultSpeed);
        defETA.setChecked(defaultETA);
    }

    //PRE:this page exists
    //POST:applys the current settings on the screen to the database
    public void applySettings(View view){
        //get all textviews and switches
        TextView defTime = (TextView)findViewById(R.id.timeText);
        TextView custMsg = (TextView)findViewById(R.id.custMessage);
        Switch defLocation = (Switch)findViewById(R.id.locationSwitch);
        Switch defSpeed = (Switch)findViewById(R.id.speedSwitch);
        Switch defETA = (Switch)findViewById(R.id.ETASwitch);

        String stringTime = defTime.getText().toString();
        int defaultTime = Integer.parseInt(stringTime);
        String customMessage = custMsg.getText().toString();
        Boolean defaultLocation = defLocation.isChecked();
        Boolean defaultSpeed = defSpeed.isChecked();
        Boolean defaultETA = defETA.isChecked();

        DefaultSettings defaultSettings = new DefaultSettings(1,defaultTime,defaultLocation,defaultSpeed,defaultETA,customMessage);
        AppDataBase db = Room.databaseBuilder(this, AppDataBase.class, "db-data").allowMainThreadQueries().build();
        DefaultSettingsDao defaultDao = db.getDefaultDao();
        defaultDao.updateDefaultSettings(defaultSettings);

        TextView doneMsg = (TextView)findViewById(R.id.congratsMsg);
        doneMsg.setText("Congratulations you have updated your default settings!");

    }
}



