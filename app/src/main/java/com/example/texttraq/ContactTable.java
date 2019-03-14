package com.example.texttraq;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ContactTable {
@PrimaryKey(autoGenerate = true)
    public int ContactID;
@ColumnInfo
    public String Name;
@ColumnInfo
    public String Number;
@ColumnInfo
    public Boolean TimeBetween;
@ColumnInfo
    public Boolean Location;
@ColumnInfo
    public Boolean Speed;
@ColumnInfo
    public Boolean ETA;
@ColumnInfo
    public String CustomMessage;
}
