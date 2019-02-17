package developer.code.kpchandora.locationassignment.roomdb.utils;

import developer.code.kpchandora.locationassignment.ui.LoginActivity;

public class Utils {
    public static final String DATABASE_NAME ="location_db"+ LoginActivity.USER_KEY[0];
    public static final int DB_VERSION = 1;

    public static final String LAT_LNG_DELIMITER = "&%";
    public static final String LAT_DELIMITER = "#@";
    public static final String LNG_DELIMITER = "@%";
}
