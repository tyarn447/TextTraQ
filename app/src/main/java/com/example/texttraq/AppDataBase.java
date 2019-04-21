package com.example.texttraq;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {DefaultSettings.class, ContactTable.class, StateTable.class}, version = 2, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {
    public abstract DefaultSettingsDao getDefaultDao();
    public abstract ContactTableDao getContactDao();
    public abstract StateTableDao getStateDao();
}
