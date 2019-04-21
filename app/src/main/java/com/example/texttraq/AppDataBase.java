package com.example.texttraq;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {DefaultSettings.class, ContactTable.class, StateTable.class}, version = 2, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {

    public abstract ContactTableDao wordDao();
    private static AppDataBase INSTANCE;

    static AppDataBase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDataBase.class, "word_database")
                            // Wipes and rebuilds instead of migrating
                            // if no Migration object.
                            // Migration is not part of this practical.
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract DefaultSettingsDao getDefaultDao();
    public abstract ContactTableDao getContactDao();
    public abstract StateTableDao getStateDao();
}
