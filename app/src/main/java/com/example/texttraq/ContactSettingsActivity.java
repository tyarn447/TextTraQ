package com.example.texttraq;


import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class ContactSettingsActivity extends AppCompatActivity {
    String name;
    String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_settings);
        TextView tv = findViewById(R.id.settingsTitle);
        Intent intent = getIntent();//gets intent
        name = intent.getStringExtra(ContactsActivity.EXTRAMESSAGE);
        number = intent.getStringExtra(ContactsActivity.EXTRAMESSAGE2);
        tv.setText(name);

        AppDataBase db = Room.databaseBuilder(this, AppDataBase.class, "db-data").allowMainThreadQueries().build();
        ContactTableDao contactTableDao = db.getContactDao();
        ContactTable aUser = contactTableDao.getUserSettings(name,number);
        String custMessage = aUser.getCustomMessage();
        int timeBetween = aUser.getTime();
        Boolean speed = aUser.getSpeed();
        Boolean location = aUser.getLocation();
        Boolean ETA = aUser.getETA();
        //have all things now get views

        TextView editMsg = (TextView)findViewById(R.id.custMessage);
        TextView editTime = (TextView)findViewById(R.id.timeText);
        Switch editLoc = (Switch)findViewById(R.id.locationSwitch);
        Switch editETA = (Switch)findViewById(R.id.ETASwitch);
        Switch editSpeed = (Switch)findViewById(R.id.speedSwitch);
        //got all views

        //set all views
        editMsg.setText(custMessage);
        editTime.setText(Integer.toString(timeBetween));
        editLoc.setChecked(location);
        editSpeed.setChecked(speed);
        editETA.setChecked(ETA);
        //all things have been changed to reflect the database


    }

    public void goToContacts(View view) {
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
    }

    public void goToMain(View view) {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(this, settingsActivity.class);
        startActivity(intent);
    }

    //PRE:this page exists
    //POST:applys the current settings on the screen to the database
    public void applySettings(View view){
        //get all textviews and switches
        TextView time = (TextView)findViewById(R.id.timeText);
        TextView msg = (TextView)findViewById(R.id.custMessage);
        Switch location = (Switch)findViewById(R.id.locationSwitch);
        Switch speed = (Switch)findViewById(R.id.speedSwitch);
        Switch ETA = (Switch)findViewById(R.id.ETASwitch);

        String stringTime = time.getText().toString();
        int contactTime = Integer.parseInt(stringTime);
        String customMessage = msg.getText().toString();
        Boolean contactLocation = location.isChecked();
        Boolean contactSpeed = speed.isChecked();
        Boolean contactETA = ETA.isChecked();

        ContactTable contactSettings = new ContactTable(name,number,contactTime,contactLocation,contactSpeed,contactETA,customMessage);
        AppDataBase db = Room.databaseBuilder(this, AppDataBase.class, "db-data").allowMainThreadQueries().build();
        ContactTableDao contactTableDao = db.getContactDao();
        //updates entry based on matching name and number
        contactTableDao.updateUserSettings(contactSettings);

        TextView doneMsg = (TextView)findViewById(R.id.congratsMsg);
        doneMsg.setText("Congratulations you have updated your default settings!");

    }


    public void goToContactSettings(View view) {
        Intent intent = new Intent(this, ContactSettingsActivity.class);
        startActivity(intent);
    }
}
