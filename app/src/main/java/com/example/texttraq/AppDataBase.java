package com.example.texttraq;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DefaultSettings.class, ContactTable.class, StateTable.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract DefaultSettingsDao defaultDao();
    public abstract ContactTableDao contactDao();
    public abstract StateTableDao stateDao();
}
