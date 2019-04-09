package com.example.texttraq;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
//import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class ContactsActivity extends AppCompatActivity{
    public static final int PICK_CONTACT = 1;
    private static final String LOG_TAG =
            MainActivity.class.getSimpleName();
    public static String EXTRAMESSAGE = "Name";
    public static String EXTRAMESSAGE2 = "Number";
    //these two are for passing to intent that goes to contactsettings activity
    String recyclerViewList[];



    /* Notes For Alex From Taylor
    First on the recycler view make sure each button shows the name and number so we have a way of getting
    both the name and number from the button,
    Then when you are able to click each one individual you want to add both the name and number into the intent,
    ie. you want to add the Name of the person into the EXTRAMESSAGE i have defined above, and you want to add the
    Number of the person into the EXTRAMESSAGE2 I have defined above as well, once those are both added into the intent
    you pass the intent along to the contactSettings activity, I have created the contact settings activity so that it will
    take both those extramessages you have passed along and make the page header have the persons name and so giving
    the page the name and number is crucial because it allows me to grab their info from the database so that we can
    initialze the page with it so it looks correct, also allows me to update their settings after when the apply button is pressed,
    that page is essentially finished just need to get the recycler view and pressing each one working... think you need
    to do something with an onclicklistener for this.

    AppDataBase db = Room.databaseBuilder(this, AppDataBase.class, "db-data").allowMainThreadQueries().build();
    ContactTableDao contactTableDao = db.getContactDao();


     */



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        RecyclerView recyclerView = findViewById(R.id.aRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AppDataBase db = Room.databaseBuilder(this, AppDataBase.class, "db-data").allowMainThreadQueries().build();
        ContactTableDao contactTableDao = db.getContactDao();
        ContactTable users[] = contactTableDao.getAllContacts();

        for (int i = 0; i < users.length; i++){
            String string;
            string = users[i].getName() + "," + users[i].getNumber();
            recyclerViewList[i] = string;
        }

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

    public void findContact(View view){
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
                        ContactTable contactTable = new ContactTable(name, phoneNumber, defTime, defLoc, defSpeed, defETA, defCustMsg);
                        //insert new contact into database
                        if(contactTableDao.getUserSettings(name,phoneNumber) == null){
                            //ASSERT:This user is not in table yet
                            contactTableDao.insert(contactTable);
                        }


                        //example for alex
                        //MUST ADD A USER TO DATABASE BEFOREHAND OR THIS WILL NOT WORK


                        ContactTable users[] = contactTableDao.getAllContacts();
                        ContactTable aUser = users[0];
                        String userName = aUser.getName();
                        String userNum = aUser.getNumber();
                        String message = userName + userNum + "athing";
                        Log.d(LOG_TAG, message);

                        //example for alex end


                    }
                }
            }
        }
    }

    public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.WordViewHolder>  {

        public WordListAdapter(Context context, LinkedList<String> wordList) {
            mInflater = LayoutInflater.from(context);
        }

        private LayoutInflater mInflater;

        class WordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public final TextView wordItemView;
            final WordListAdapter mAdapter;

            public WordViewHolder(View itemView, WordListAdapter adapter) {
                super(itemView);
                wordItemView = itemView.findViewById(R.id.ContactInfo);
                this.mAdapter = adapter;
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                // Get the position of the item that was clicked.
                int mPosition = getLayoutPosition();

                // Use that to access the affected item in mWordList.
                String element = recyclerViewList[mPosition];

                // Notify the adapter, that the data has changed so it can
                // update the RecyclerView to display the data.
                mAdapter.notifyDataSetChanged();
            }
        }

        @NonNull
        @Override
        public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View mItemView = mInflater.inflate(R.layout.contact_info, parent, false);
            return new WordViewHolder(mItemView, this);
        }

        @Override
        public void onBindViewHolder(WordViewHolder holder, int position) {
            String mCurrent = recyclerViewList[position];
            holder.wordItemView.setText(mCurrent);
        }

        @Override
        public int getItemCount() {
            return recyclerViewList.length;
        }
    }
}
