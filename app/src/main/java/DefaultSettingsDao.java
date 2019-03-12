import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface DefaultSettingsDao {
    //Inserts a defaultSettings row into the table.
    @Insert
    public void createDefaultSettings(DefaultSettings defaultSettings);
}
