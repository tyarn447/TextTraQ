package com.example.texttraq;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ContactTableDao {
    @Insert
    void insert(ContactTable contactTable);

    @Query("SELECT * FROM ContactTable WHERE Name = :aName AND Number = :aNumber")
    ContactTable getUserSettings(String aName, String aNumber);

    //updates the user settings by matching the name and number
    @Update
    void updateUserSettings(ContactTable contactTable);

    //PRE:table exists
    //POST:gets all contacts from contactTable
    @Query("SELECT * from ContactTable ORDER BY Name ASC")
    ContactTable[] getAllContacts();

    //PRE:tables exists
    //POST:used to delete contacts from the database
    @Query("DELETE FROM ContactTable WHERE Name = :aName AND Number = :aNumber")
    void deleteContact(String aName, String aNumber);

}
