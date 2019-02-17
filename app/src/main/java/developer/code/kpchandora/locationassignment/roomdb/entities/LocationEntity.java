package developer.code.kpchandora.locationassignment.roomdb.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

import developer.code.kpchandora.locationassignment.roomdb.utils.Utils;

@Entity(tableName = LocationEntity.LOCATION_TABLE_NAME)
public class LocationEntity implements Serializable{

    /*Name of location table*/
    public static final String LOCATION_TABLE_NAME = "location_table";

    /* The name of ID column*/
    public static final String LOCATION_TABLE_ID = "table_id";

    /*The name of latitude column*/
    public static final String LOCATION_LAT_COLUMN = "lat_column";

    /*The name of longitude column*/
    public static final String LOCATION_LNG_COLUMN = "lng_column";

    /*The name of address column*/
    public static final String LOCATION_ADDRESS_COLUMN = "address_column";

    /*The name of time stamp column*/
    public static final String LOCATION_TIME_STAMP_COLUMN = "location_time_stamp";

    /*The unique ID of the location table*/
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = LOCATION_TABLE_ID)
    private int id;

    /*The latitude of location*/
    @ColumnInfo(name = LOCATION_LAT_COLUMN)
    private double lat;

    /*The longitude of location*/
    @ColumnInfo(name = LOCATION_LNG_COLUMN)
    private double lng;

    /*The address of fetched coordinates*/
    @ColumnInfo(name = LOCATION_ADDRESS_COLUMN)
    private String address;

    @ColumnInfo(name = LOCATION_TIME_STAMP_COLUMN)
    private String timeStamp;

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
