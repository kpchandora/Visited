package developer.code.kpchandora.locationassignment.service;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Locale;

import developer.code.kpchandora.locationassignment.MainActivity;
import developer.code.kpchandora.locationassignment.receiver.ConnectivityReceiver;
import developer.code.kpchandora.locationassignment.roomdb.database.LocationDatabase;
import developer.code.kpchandora.locationassignment.roomdb.entities.LocationEntity;

public class LocationService extends Service {

    private static final String TAG = "LocationService";
    public static final String CONNECTION_BROADCAST = "connection_available";

    private FusedLocationProviderClient fusedLocationProvider;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private boolean isNetworkAvailable;
    private LocationDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(connectionReceiver, new IntentFilter(CONNECTION_BROADCAST));
        isNetworkAvailable = ConnectivityReceiver.isConnected(this);
        database = LocationDatabase.getInstance(this);
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        //locationRequest.setSmallestDisplacement(0f);

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

        public MyRunnable(Location location) {
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
                database.locationDao().insertLocation(entity);
            }

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(connectionReceiver);
        fusedLocationProvider.removeLocationUpdates(locationCallback);
        super.onDestroy();
    }
}
