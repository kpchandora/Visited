package developer.code.kpchandora.locationassignment.roomdb.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import developer.code.kpchandora.locationassignment.LoginActivity;
import developer.code.kpchandora.locationassignment.MainActivity;

@Entity(tableName = LocationHistory.LOCATION_HISTORY_TABLE_NAME)
public class LocationHistory {

    /*Name of location table*/
    public static final String LOCATION_HISTORY_TABLE_NAME = "location_history_table";

    /* The name of ID column*/
    public static final String HISTORY_TABLE_ID = "history_id";

    /*The name of time stamp while location fetching started column*/
    public static final String HISTORY_TIME_COLUMN = "history_time";

    /*The name of fetch locations column*/
    public static final String FETCH_LOCATION_COLUMN = "fetched_location";

    /*The name of address column*/
    public static final String HISTORY_ADDRESS_COLUMN = "history_address";

    /*The unique ID of the table*/
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = HISTORY_TABLE_ID)
    private int id;

    /*The time stamp of location list table*/
    @ColumnInfo(name = HISTORY_TIME_COLUMN)
    private String timeStamp;

    @ColumnInfo(name = FETCH_LOCATION_COLUMN)
    private String locationsString;

    @ColumnInfo(name = HISTORY_ADDRESS_COLUMN)
    private String historyAddress;

    public String getHistoryAddress() {
        return historyAddress;
    }

    public void setHistoryAddress(String historyAddress) {
        this.historyAddress = historyAddress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getLocationsString() {
        return locationsString;
    }

    public void setLocationsString(String locationsString) {
        this.locationsString = locationsString;
    }
}
