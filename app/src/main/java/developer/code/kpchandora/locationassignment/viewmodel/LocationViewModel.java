package developer.code.kpchandora.locationassignment.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import developer.code.kpchandora.locationassignment.roomdb.database.LocationDatabase;
import developer.code.kpchandora.locationassignment.roomdb.entities.LocationEntity;

public class LocationViewModel extends AndroidViewModel{

    private LiveData<List<LocationEntity>>listLiveData;
    public LocationViewModel(@NonNull Application application) {
        super(application);
        listLiveData = LocationDatabase.getInstance(application)
                .locationDao().getAllLocations();
    }

    public LiveData<List<LocationEntity>> getListLiveData() {
        return listLiveData;
    }
}
