package com.example.texttraq;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ContactTableDao {
    @Insert
    void insert(ContactTable contactTable);

    @Query("SELECT * FROM ContactTable WHERE Name = :aName AND Number = :aNumber ")
    ContactTable getUserSettings(String aName, String aNumber);

    //updates the user settings by matching the name and number
    @Update
    void updateUserSettings(ContactTable contactTable);

    //PRE:tables exists
    //POST:used to delete contacts from the database
    @Query("DELETE FROM ContactTable WHERE Name = :aName AND Number = :aNumber")
    void deleteContact(String aName, String aNumber);

    //PRE:table exists
    //POST:gets all contacts from contactTable
    @Query("SELECT * FROM ContactTable ORDER BY Name")
    public ContactTable[] getAllContacts();

    //PRE: Table exists
    //POST:gets contact names from contactTable
    @Query("SELECT Name FROM ContactTable")
    List<String> getContactNames();

    //PRE: Table exists
    //POST:gets contact numbers from contactTable
    @Query("SELECT Number FROM ContactTable")
    String[] getContactNumbers();

    //PRE: Table exists
    //POST:gets contact custom message from contactTable
    @Query("SELECT CustomMessage FROM ContactTable")
    String[] getContactCustomMessage();

    //PRE: Table exists
    //POST:gets contact location booleans from contactTable
    @Query("SELECT Location FROM ContactTable")
    List<Boolean> getContactLocation();

    //PRE: Table exists
    //POST:gets contact speed boolean from contactTable
    @Query("SELECT Speed FROM ContactTable")
    Boolean[] getContactSpeed();

    //PRE: Table exists
    //POST:gets contact ETA booleans from contactTable
    @Query("SELECT ETA FROM ContactTable")
    Boolean[] getContactETA();

    //PRE: Table exists
    //POST:gets contact location booleans from contactTable
    @Query("SELECT TimeBetween FROM ContactTable")
    List<Integer> getContactTimeBetween();

}
