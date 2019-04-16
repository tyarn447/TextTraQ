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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
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

public class ContactsActivity extends AppCompatActivity {
    public static final int PICK_CONTACT = 1;
    private static final String LOG_TAG =
            MainActivity.class.getSimpleName();
    public static String EXTRAMESSAGE = "Name";
    public static String EXTRAMESSAGE2 = "Number";
    //these two are for passing to intent that goes to contactsettings activity
    private WordViewModel mWordViewModel;
    public List<String> aList = new ArrayList<String>();
    public String[] recyclerViewList;



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

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final WordListAdapter adapter = new WordListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mWordViewModel = ViewModelProviders.of(this).get(WordViewModel.class);
        mWordViewModel.getAllWords().observe(this, new Observer<List<ContactTable>>() {
            @Override
            public void onChanged(@Nullable final List<ContactTable> words) {
                // Update the cached copy of the words in the adapter.
                adapter.setWords(words);
            }
        });
        /*
        for (int i = 0; i < users.length; i++) {
            String aString;
            aString = users[i].getName() + "," + users[i].getNumber();
            aList.add(aString);
        }
        recyclerViewList = aList.toArray(new String[aList.size()]);
        */
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


                        //example for alex
                        //MUST ADD A USER TO DATABASE BEFOREHAND OR THIS WILL NOT WORK


                        //example for alex end


                    }
                }
            }
        }
    }

    public static class WordRepository {

        private ContactTableDao mWordDao;
        private LiveData<List<ContactTable>> mAllWords;

        WordRepository(Application application) {
            AppDataBase db = AppDataBase.getDatabase(application);
            mWordDao = db.getContactDao();
            mAllWords = mWordDao.getAllContacts();
        }

        LiveData<List<ContactTable>> getAllWords() {
            return mAllWords;
        }

        public void insert(ContactTable contactTable) {
            new insertAsyncTask(mWordDao).execute(contactTable);
        }

        private static class insertAsyncTask extends AsyncTask<ContactTable, Void, Void> {

            private ContactTableDao mAsyncTaskDao;

            insertAsyncTask(ContactTableDao dao) {
                mAsyncTaskDao = dao;
            }

            @Override
            protected Void doInBackground(final ContactTable... params) {
                mAsyncTaskDao.insert(params[0]);
                return null;
            }
        }
    }

    public class WordViewModel extends AndroidViewModel {

        private WordRepository mRepository;

        private LiveData<List<ContactTable>> mAllWords;

        public WordViewModel (Application application) {
            super(application);
            mRepository = new WordRepository(application);
            mAllWords = mRepository.getAllWords();
        }

        LiveData<List<ContactTable>> getAllWords() { return mAllWords; }

        public void insert(ContactTable contactTable) { mRepository.insert(contactTable); }
    }


    public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.WordViewHolder> {

        private final LayoutInflater mInflater;
        private List<ContactTable> mWords; // Cached copy of words

        WordListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = mInflater.inflate(R.layout.contact_info, parent, false);
            return new WordViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(WordViewHolder holder, int position) {
            if (mWords != null) {
                ContactTable current = mWords.get(position);
                holder.wordItemView.setText(current.getName());
            } else {
                // Covers the case of data not being ready yet.
                holder.wordItemView.setText("No Word");
            }
        }

        void setWords(List<ContactTable> words) {
            mWords = words;
            notifyDataSetChanged();
        }

        // getItemCount() is called many times, and when it is first called,
        // mWords has not been updated (means initially, it's null, and we can't return null).
        @Override
        public int getItemCount() {
            if (mWords != null)
                return mWords.size();
            else return 0;
        }

        class WordViewHolder extends RecyclerView.ViewHolder {
            private final TextView wordItemView;

            private WordViewHolder(View itemView) {
                super(itemView);
                wordItemView = itemView.findViewById(R.id.textView);
            }
        }
    }


    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final ContactTableDao mDao;
        String[] words;

        PopulateDbAsync(AppDataBase db) {
            mDao = db.getContactDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            // Start the app with a clean database every time.
            // Not needed if you only populate the database
            // when it is first created
            mDao.deleteAll();

            for (int i = 0; i <= words.length - 1; i++) {
                ContactTable contactTable = new ContactTable(words[i]);
                mDao.insert(contactTable);
            }
            return null;
        }
    }

}
