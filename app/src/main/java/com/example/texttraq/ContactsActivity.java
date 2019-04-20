package com.example.texttraq;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class ContactsActivity extends AppCompatActivity {
    public static final int PICK_CONTACT = 1;
    private static final String LOG_TAG =
            MainActivity.class.getSimpleName();
    public static String EXTRAMESSAGE = "Name";
    public static String EXTRAMESSAGE2 = "Number";
    //these two are for passing to intent that goes to contactsettings activity

    public List<String> aList = new ArrayList<String>();
    public String[] recyclerViewList;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        AppDataBase db = Room.databaseBuilder(this, AppDataBase.class, "db-data").allowMainThreadQueries().build();
        ContactTableDao contactTableDao = db.getContactDao();
        final ContactTable users[] = contactTableDao.getAllContacts();

        for (int i = 0; i < users.length; i++) {
            String aString;
            aString = users[i].getName() + "," + users[i].getNumber();
            aList.add(aString);
        }
        recyclerViewList = aList.toArray(new String[aList.size()]);

        ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.activity_listview,recyclerViewList);
        final ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nameNum = (String) listView.getItemAtPosition(position);
                Log.d(LOG_TAG, nameNum);
                String separated[] = nameNum.split(",");
                String name = separated[0];
                String num = separated[1];
                sendNameNum(name,num);
            }

        });
    }

    public void sendNameNum(String name, String num){
        Intent newIntent = new Intent(this,ContactSettingsActivity.class);
        newIntent.putExtra(EXTRAMESSAGE,name);
        newIntent.putExtra(EXTRAMESSAGE2,num);
        startActivity(newIntent);
        finish();
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

    public void findContact(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        ContentResolver cr = getContentResolver();

        if (reqCode == PICK_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contactData = data.getData();
                Cursor c = cr.query(contactData, null, null, null, null);
                if (((Cursor) c).moveToFirst()) {
                    String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                    if (phones.moveToFirst()) {
                        String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        //add stuff to database
                        AppDataBase db = Room.databaseBuilder(this, AppDataBase.class, "db-data").allowMainThreadQueries().build();
                        DefaultSettingsDao defaultDao = db.getDefaultDao();
                        DefaultSettings defaultSettings = defaultDao.getDefaultSettings();
                        String defCustMsg = defaultSettings.getDefaultCustomMessage();
                        int defTime = defaultSettings.getDefaultTime();
                        Boolean defSpeed = defaultSettings.getDefaultSpeed();
                        Boolean defLoc = defaultSettings.getDefaultLocation();
                        Boolean defETA = defaultSettings.getDefaultETA();

                        //make new object for contact
                        ContactTableDao contactTableDao = db.getContactDao();
                        ContactTable newUser = new ContactTable(name, phoneNumber, defTime, defLoc, defSpeed, defETA, defCustMsg);
                        //insert new contact into database
                        if (contactTableDao.getUserSettings(name, phoneNumber) == null) {
                            //ASSERT:This user is not in table yet
                            contactTableDao.insert(newUser);
                            //ContactTable users[] = contactTableDao.getAllContacts();
                            //ContactTable aUser = users[0];
                            //String userName = aUser.getName();
                            //String userNum = aUser.getNumber();
                            //String message = userName + userNum + "athing";
                            //Log.d(LOG_TAG, message);
                            //Button aButton = (Button)findViewById(R.id.contactsButton);

                        }
                    }
                }
            }
        }
        finish();
        startActivity(getIntent());
    }
}
