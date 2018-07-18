package developer.code.kpchandora.locationassignment.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import developer.code.kpchandora.locationassignment.roomdb.database.LocationDatabase;
import developer.code.kpchandora.locationassignment.roomdb.entities.LocationHistory;

public class HistoryViewModel extends AndroidViewModel {

    private LiveData<List<LocationHistory>> historyLiveData;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        historyLiveData = LocationDatabase.getInstance(application)
                .historyDao().getAllHistory();
    }

    public LiveData<List<LocationHistory>> getHistoryLiveData() {
        return historyLiveData;
    }
}
