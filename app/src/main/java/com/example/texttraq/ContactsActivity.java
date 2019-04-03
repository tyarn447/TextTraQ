package com.example.texttraq;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Layout;
import android.util.AndroidException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
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

public class ContactsActivity extends AppCompatActivity {
    public static final int PICK_CONTACT = 1;
    private static final String LOG_TAG =
            MainActivity.class.getSimpleName();
    public static final String EXTRAMESSAGE = "Nothing now";
    public static final String EXTRAMESSAGE2 = "Nothing now";
    //these two are for passing to intent that goes to contactsettings activity



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

     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
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
    public void onActivityResult(int reqCode, int resultCode, Intent data){
        super.onActivityResult(reqCode,resultCode,data);
        ContentResolver cr = getContentResolver();

        if(reqCode == PICK_CONTACT){
            if(resultCode == Activity.RESULT_OK){
                Uri contactData = data.getData();
                Cursor c = cr.query(contactData,null,null,null,null);
                if(((Cursor) c).moveToFirst()){
                    String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
                    if(phones.moveToFirst()) {
                        String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Button nameButton = findViewById(R.id.button);
                        Button numberButton = findViewById(R.id.button2);
                        nameButton.setText(name);
                        numberButton.setText(phoneNumber);

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
                        ContactTable contactTable = new ContactTable(name,phoneNumber,defTime,defLoc,defSpeed,defETA,defCustMsg);
                        //insert new contact into database
                        contactTableDao.insert(contactTable);
                    }
                }
            }
        }

    }

    // New Code

    @Entity(tableName = "word_table")
    public class Word {

        @PrimaryKey
        @NonNull
        @ColumnInfo(name = "word")
        private String mWord;

        public Word(@NonNull String word) {this.mWord = word;}

        public String getWord(){return this.mWord;}
    }

    @Dao
    public interface WordDao{

        @Insert
        void insert (Word word);

        @Query("DELETE FROM word_table")
        void deleteAll();

        @Query("SELECT * from word_table ORDER BY word ASC")
        List<Word> getAllWords();
    }

    @Query("SELECT * from word_table ORDER BY word ASC")
    LiveData<List<Word>> getAllWord();

    @Database(entities = {Word.class}, version = 1, exportSchema = false)
    public abstract static class WordRoomDatabase extends RoomDatabase {

        public abstract WordDao wordDao();
        private static WordRoomDatabase INSTANCE;

        static WordRoomDatabase getDatabase(final Context context) {
            if (INSTANCE == null) {
                synchronized (WordRoomDatabase.class) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                WordRoomDatabase.class, "word_database")
                                .fallbackToDestructiveMigration().build();
                    }
                }
            }
            return INSTANCE;
        }

    }

    public class WordRepository {
        private WordDao mWordDao;
        private LiveData<List<Word>> mAllWords;
        WordRepository(Application application) {
            WordRoomDatabase db = WordRoomDatabase.getDatabase(application);
            mWordDao = db.wordDao();
            mAllWords = mWordDao.getAllWords();
        }

        LiveData<List<Word>> getAllWords() {
            return mAllWords;
        }

        public void insert (Word word) {
            new insertAsyncTask(mWordDao).execute(word);
        }

        private static class insertAsyncTask extends AsyncTask<Word, Void, Void> {
            private WordDao mAsyncTaskDao;
            insertAsyncTask(WordDao dao) {
                mAsyncTaskDao = dao;
            }

            @Override
            protected  Void doInBackground(final Word... params) {
                mAsyncTaskDao.insert(params[0]);
                return null;
            }
        }
    }

    public class WordViewModel extends AndroidViewModel {
        private WordRepository mRepository;
        private LiveData<List<Word>> mAllWords;
        public WordViewModel (Application application) {
            super(application);
            mRepository = new WordRepository(application);
            mAllWords = mRepository.getAllWords();
        }

        LiveData<List<Word>> getAllWords() { return mAllWords; }
        public void insert(Word word) {mRepository.insert(word);}
    }

    public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.WordViewHolder> {

        private final LayoutInflater mInflator;
        private List<Word> mWords; // Cached copy of words

        WordListAdapter(Context context) { mInflator = LayoutInflater.from(context); }

        @Override
        public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = mInflator.inflate(R.layout.recyclerview_item
        }
    }

}
