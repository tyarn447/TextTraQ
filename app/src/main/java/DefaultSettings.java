import java.sql.Time;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DefaultSettings {
    //this doesnt need to autoIncrement because there will only be 1
@PrimaryKey
    public int DefaultID;
@ColumnInfo
    public Time DefaultTime;
@ColumnInfo
    public Boolean DefaultLocation;
@ColumnInfo
    public Boolean DefaultSpeed;
@ColumnInfo
    public Boolean DefaultETA;
@ColumnInfo
    public String DefaultCustomMessage;
}
