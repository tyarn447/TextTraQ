import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class StateTable {
    //doesnt need to autoincr cause its the only one
@PrimaryKey
    public int StateID;
@ColumnInfo
    public Boolean Started;
@ColumnInfo
    public Boolean Stopped;
@ColumnInfo
    public Boolean Paused;



}
