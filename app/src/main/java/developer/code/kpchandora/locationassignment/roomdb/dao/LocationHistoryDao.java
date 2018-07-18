package developer.code.kpchandora.locationassignment.roomdb.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import developer.code.kpchandora.locationassignment.roomdb.entities.LocationHistory;

@Dao
public interface LocationHistoryDao {

    /**
     * Inserts a new LocationHistory into the table
     *
     * @param history
     * @return The row ID of the new inserted {@link LocationHistory}
     */
    @Insert
    long insertLocationHistory(LocationHistory history);

    /**
     * Select single location history
     *
     * @return A {@link LocationHistory} from the table
     */
    @Query("SELECT * FROM " + LocationHistory.LOCATION_HISTORY_TABLE_NAME +
            " WHERE " + LocationHistory.HISTORY_TIME_COLUMN + " = :locationHistory")
    LocationHistory getSingleHistory(String locationHistory);

    /**
     * Select all locations
     *
     * @return A {@link List} of all the LocationsHistory in the table
     */
    @Query("SELECT * FROM " + LocationHistory.LOCATION_HISTORY_TABLE_NAME +
            " ORDER BY " + LocationHistory.HISTORY_TABLE_ID + " DESC")
    LiveData<List<LocationHistory>> getAllHistory();
}
