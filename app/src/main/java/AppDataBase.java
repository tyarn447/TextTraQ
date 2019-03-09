import androidx.room.Database;

@Database(entities = {DefaultSettings.class,ContactTable.class, StateTable.class}, version = 1)
public class AppDataBase extends RoomDatabase{
    public abstract DefaultSettingsDao defaultDao();
    public abstract ContactTableDao contactDao();
    public abstract StateTableDao stateDao();
}
