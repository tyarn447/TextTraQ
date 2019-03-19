package com.example.texttraq;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface DefaultSettingsDao {
    //Inserts a defaultSettings row into the table.
    @Insert
    void insert(DefaultSettings defaultSettings);

    @Query("SELECT * FROM DefaultSettings WHERE DefaultID=1")
    DefaultSettings getDefaultSettings();

    @Update
    void updateDefaultSettings(DefaultSettings defaultSettings);
}
