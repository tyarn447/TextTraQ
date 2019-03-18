package com.example.texttraq;

import java.sql.Time;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;

@Entity
public class DefaultSettings {
    //this doesnt need to autoIncrement because there will only be 1
    @PrimaryKey
    public int DefaultID;
    @ColumnInfo
    public int DefaultTime;
    @ColumnInfo
    public Boolean DefaultLocation;
    @ColumnInfo
    public Boolean DefaultSpeed;
    @ColumnInfo
    public Boolean DefaultETA;
    @ColumnInfo
    public String DefaultCustomMessage;


    //PRE:none
    //POST:Base Constructor
    public DefaultSettings(){}

    //PRE:none
    //POST:Constructs the object
    public DefaultSettings(int pDefaultID, int pDefaultTime, Boolean pDefaultLocation, Boolean pDefaultSpeed, Boolean pDefaultETA, String pDefaultCustomMessage){
        DefaultID = pDefaultID;
        DefaultTime = pDefaultTime;
        DefaultLocation = pDefaultLocation;
        DefaultSpeed = pDefaultSpeed;
        DefaultETA = pDefaultETA;
        DefaultCustomMessage = pDefaultCustomMessage;
    }


    public String getDefaultCustomMessage(){
        return DefaultCustomMessage;
    }


}
