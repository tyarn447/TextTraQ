package com.example.texttraq;

import java.sql.Time;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DefaultSettings {
    //this doesnt need to autoIncrement because there will only be 1
    @PrimaryKey
    private int DefaultID;
    @ColumnInfo
    private int DefaultTime;
    @ColumnInfo
    private Boolean DefaultLocation;
    @ColumnInfo
    private Boolean DefaultSpeed;
    @ColumnInfo
    private Boolean DefaultETA;
    @ColumnInfo
    private String DefaultCustomMessage;
}
