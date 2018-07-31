package developer.code.kpchandora.locationassignment.service;

import android.app.Service;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import developer.code.kpchandora.locationassignment.MainActivity;
import developer.code.kpchandora.locationassignment.receiver.ConnectivityReceiver;
import developer.code.kpchandora.locationassignment.roomdb.database.LocationDatabase;
import developer.code.kpchandora.locationassignment.roomdb.entities.LocationEntity;
import developer.code.kpchandora.locationassignment.roomdb.entities.LocationHistory;
import developer.code.kpchandora.locationassignment.roomdb.utils.Utils;

public class LocationService extends Service {

    private static final String TAG = "LocationService";
    public static final String CONNECTION_BROADCAST = "connection_available";

    private FusedLocationProviderClient fusedLocationProvider;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private boolean isNetworkAvailable;
    private LocationDatabase database;
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        registerReceiver(connectionReceiver, new IntentFilter(CONNECTION_BROADCAST));
        isNetworkAvailable = ConnectivityReceiver.isConnected(this);
        database = LocationDatabase.getInstance(this);
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
//        locationRequest.setSmallestDisplacement(20f);

        getLocationCallback();
        if (MainActivity.checkPermission()) {
            fusedLocationProvider.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }


    private void getLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    Log.i(TAG, "onLocationResult: " + location.getLatitude());
                    AsyncTask.execute(new MyRunnable(location));
                }
            }
        };
    }

    private BroadcastReceiver connectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isNetworkAvailable = intent.getBooleanExtra("isOnline", false);
            Log.i(TAG, "onReceive: " + isNetworkAvailable);

        }
    };

    private class MyRunnable implements Runnable {

        private Location location;

        private MyRunnable(Location location) {
            this.location = location;
        }

        @Override
        public void run() {
            LocationEntity entity = new LocationEntity();
            entity.setLat(location.getLatitude());
            entity.setLng(location.getLongitude());
            Log.i(TAG, "run: " + isNetworkAvailable);
            if (isNetworkAvailable) {
                Geocoder geocoder = new Geocoder(LocationService.this, Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    String strAddress = addresses.get(0).getAddressLine(0);
                    entity.setAddress(strAddress);
                    database.locationDao().insertLocation(entity);
                    Log.i(TAG, "run: " + strAddress);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                entity.setAddress("NA");
                database.locationDao().insertLocation(entity);
            }

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class InsertDataRunnable implements Runnable {

        @Override
        public void run() {

            Date currentTime = Calendar.getInstance().getTime();
            LocationHistory history = new LocationHistory();
            history.setTimeStamp(currentTime.toString());

            List<LocationEntity> locationEntities = LocationDatabase.getInstance(getApplication()).locationDao().getAllEntities();

            int locationEntitiesSize = locationEntities.size();

            if (locationEntitiesSize < 1){
                return;
            }

            StringBuilder addressStringBuilder = new StringBuilder();
            StringBuilder latBuilder = new StringBuilder();
            StringBuilder lngBuilder = new StringBuilder();

            if (locationEntities != null) {
                for (int i = 0; i < locationEntitiesSize; i++) {
                    addressStringBuilder.append(locationEntities.get(i).getAddress()).append(Utils.LAT_LNG_DELIMITER);
                }
                for (int i = 0; i < locationEntitiesSize; i++) {
                    latBuilder.append(locationEntities.get(i).getLat()).append(Utils.LAT_DELIMITER);
                }
                for (int i = 0; i < locationEntitiesSize; i++) {
                    lngBuilder.append(locationEntities.get(i).getLng()).append(Utils.LNG_DELIMITER);
                }
                latBuilder.append(Utils.LAT_LNG_DELIMITER).append(lngBuilder);
            }

            String addressStr = addressStringBuilder.toString();
            history.setLocationsString(latBuilder.toString());
            history.setHistoryAddress(addressStr);

            LocationDatabase.getInstance(getApplication()).historyDao().insertLocationHistory(history);
            LocationDatabase.getInstance(getApplication()).locationDao().deleteAll();
        }
    }

    @Override
    public void onDestroy() {
        AsyncTask.execute(new InsertDataRunnable());
        unregisterReceiver(connectionReceiver);
        fusedLocationProvider.removeLocationUpdates(locationCallback);
        super.onDestroy();
    }
}
