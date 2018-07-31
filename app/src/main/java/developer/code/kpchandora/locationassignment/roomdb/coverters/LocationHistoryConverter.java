package developer.code.kpchandora.locationassignment.roomdb.coverters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import developer.code.kpchandora.locationassignment.roomdb.entities.LocationEntity;

public class LocationHistoryConverter {

    private static Gson gson = new Gson();

    @android.arch.persistence.room.TypeConverter
    public static List<LocationEntity>stringToSomeObjectList(String data){
        if (data == null){
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<LocationEntity>>() {}.getType();
        return gson.fromJson(data, listType);
    }

    @android.arch.persistence.room.TypeConverter
    public static String someObjectListToString(List<LocationEntity> someObjects) {
        return gson.toJson(someObjects);
    }
}
