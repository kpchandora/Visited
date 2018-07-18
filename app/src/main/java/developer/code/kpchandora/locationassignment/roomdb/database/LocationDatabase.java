package developer.code.kpchandora.locationassignment.roomdb.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.google.android.gms.common.util.DbUtils;

import developer.code.kpchandora.locationassignment.roomdb.dao.LocationDao;
import developer.code.kpchandora.locationassignment.roomdb.entities.LocationEntity;
import developer.code.kpchandora.locationassignment.roomdb.utils.Utils;

@Database(entities = {LocationEntity.class}, version = Utils.DB_VERSION)
public abstract class LocationDatabase extends RoomDatabase {

    /**
     * @return The dao for Location table
     */
    public abstract LocationDao locationDao();

    /**
     * The only instance of database
     */
    private static LocationDatabase sInstance;


    /**
     * Gets the singleton instance of the database.
     *
     * @param context The context.
     * @return The singleton instance of database.
     */
    public static synchronized LocationDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = Room
                    .databaseBuilder(context.getApplicationContext(), LocationDatabase.class, Utils.DATABASE_NAME)
                    .build();
        }
        return sInstance;
    }

}
