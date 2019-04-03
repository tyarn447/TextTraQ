package com.example.texttraq;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(primaryKeys = {"Name","Number"})
public class ContactTable {

@PrimaryKey
    public String Name;
@ColumnInfo
    public String Number;
@ColumnInfo
    public int TimeBetween;
@ColumnInfo
    public Boolean Location;
@ColumnInfo
    public Boolean Speed;
@ColumnInfo
    public Boolean ETA;
@ColumnInfo
    public String CustomMessage;

    public ContactTable(String pName, String pNumber, int pContactTime, Boolean pContactLocation, Boolean pContactSpeed, Boolean pContactETA, String pCustomMessage) {
        Name = pName;
        Number = pNumber;
        TimeBetween = pContactTime;
        Location = pContactLocation;
        Speed = pContactSpeed;
        ETA = pContactETA;
        CustomMessage = pCustomMessage;
    }

    //PRE:object exists
    //POST:returns the default custom message member data of this object
    public String getCustomMessage(){

        return CustomMessage;
    }

    //PRE:object exists
    //POST:returns the default custom message data of this object
    public int getTime(){
        return TimeBetween;
    }

    //PRE:object exists
    //POST:returns whether DefaultLocation is true or false
    public Boolean getLocation(){
        return Location;
    }

    //PRE:object exists
    //POST:returns whether DefaultSpeed is true or false
    public Boolean getSpeed(){
        return Speed;
    }

    //PRE:object exists
    //POST:returns whether DefaultETA is true or false
    public Boolean getETA(){
        return ETA;
    }

    //PRE:object exists
    //POST:returns name parameter of obj
    public String getName(){
        return Name;
    }

    //PRE:obj exists
    //POST:returns number param of obj
    public String getNumber(){
        return Number;
    }

}
