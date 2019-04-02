package com.example.texttraq;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
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
