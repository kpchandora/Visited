package developer.code.kpchandora.locationassignment.roomdb.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import developer.code.kpchandora.locationassignment.roomdb.entities.LocationEntity;

@Dao
public interface
LocationDao {

    /**
     * Inserts a new Location into the table
     *
     * @param entity
     * @return The row ID of the new inserted {@link LocationEntity}
     */
    @Insert
    long insertLocation(LocationEntity entity);


    /**
     * Select all locations
     *
     * @return A {@link List} of all the Locations in the table
     */
    @Query("SELECT * FROM " + LocationEntity.LOCATION_TABLE_NAME + " ORDER BY " + LocationEntity.LOCATION_TABLE_ID + " DESC")
    LiveData<List<LocationEntity>> getAllLocations();

    @Query("SELECT * FROM " + LocationEntity.LOCATION_TABLE_NAME + " ORDER BY " + LocationEntity.LOCATION_TABLE_ID + " DESC")
    List<LocationEntity>getAllEntities();

    /**
     * Updates a location.
     *
     * @param entity The location to be updated
     * @return A number of locations updated
     */
    @Update
    int updateLocation(LocationEntity entity);

    @Query("DELETE FROM " + LocationEntity.LOCATION_TABLE_NAME)
    void deleteAll();
}
