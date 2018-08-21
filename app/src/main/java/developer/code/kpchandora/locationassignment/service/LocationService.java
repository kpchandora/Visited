package developer.code.kpchandora.locationassignment.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import developer.code.kpchandora.locationassignment.MainActivity;
import developer.code.kpchandora.locationassignment.R;
import developer.code.kpchandora.locationassignment.receiver.ConnectivityReceiver;
import developer.code.kpchandora.locationassignment.receiver.NotificationBroadcastReceiver;
import developer.code.kpchandora.locationassignment.roomdb.database.LocationDatabase;
import developer.code.kpchandora.locationassignment.roomdb.entities.LocationEntity;
import developer.code.kpchandora.locationassignment.roomdb.entities.LocationHistory;
import developer.code.kpchandora.locationassignment.roomdb.utils.Utils;

public class LocationService extends Service {

    private static final String TAG = "LocationService";
    public static final String CONNECTION_BROADCAST = "connection_available";

    public static final String STOP_ACTION_MESSAGE = "stop_fetching";

    private FusedLocationProviderClient fusedLocationProvider;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private boolean isNetworkAvailable;
    private LocationDatabase database;
    private Context context;
    private String addressText;
    private String notiContentTitle;
    private PendingIntent pendingIntent;
    private PendingIntent stopServicePendingIntent;
    private NotificationCompat.Builder builder;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
        context = this;
        addressText = "";
        notiContentTitle = "";
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

        Intent startActivityIntent = new Intent(this, MainActivity.class);
        startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Intent stopLocationFetching = new Intent(this, NotificationBroadcastReceiver.class);
        stopLocationFetching.putExtra("action", STOP_ACTION_MESSAGE);
        stopServicePendingIntent = PendingIntent.getBroadcast(this, 1,
                stopLocationFetching, PendingIntent.FLAG_UPDATE_CURRENT);

        pendingIntent = PendingIntent.getActivity(this, 0, startActivityIntent, 0);
        builder = new NotificationCompat.Builder(LocationService.this, "noti_id");
        builder.setContentIntent(pendingIntent);
        builder.addAction(R.drawable.stop_icon, "Stop", stopServicePendingIntent);
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

            Date date = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd hh:mm:ss aa");
            String formattedDate = dateFormat.format(date);


            entity.setLat(location.getLatitude());
            entity.setLng(location.getLongitude());
            entity.setTimeStamp(formattedDate);

            Log.i(TAG, "run: " + isNetworkAvailable);
            if (isNetworkAvailable) {
                Geocoder geocoder = new Geocoder(LocationService.this, Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    String strAddress = addresses.get(0).getAddressLine(0);
                    entity.setAddress(strAddress);
                    addressText = strAddress;
                    notiContentTitle = "You are at";
                    database.locationDao().insertLocation(entity);
                    Log.i(TAG, "run: " + strAddress);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                entity.setAddress("NA");
                database.locationDao().insertLocation(entity);
                notiContentTitle = "You were at";
            }
            sendNotification();
        }
    }


    private void sendNotification() {

        builder.setSmallIcon(R.drawable.ic_pin);
        builder.setContentTitle(notiContentTitle)
                .setContentText(addressText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        startForeground(1, builder.build());

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class InsertDataRunnable implements Runnable {

        @Override
        public void run() {

            List<LocationEntity> locationEntities =
                    LocationDatabase.getInstance(getApplication()).locationDao().getAllEntities();

            if (locationEntities == null) {
                return;
            }

            int locationEntitiesSize = locationEntities.size();

            if (locationEntitiesSize < 1) {
                return;
            }

            String date = locationEntities.get(locationEntitiesSize - 1).getTimeStamp();
            Date dateFormat;
            String convertedDate = "";
            try {
                dateFormat = new SimpleDateFormat("EEE MMM dd hh:mm:ss aa").parse(date);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd hh:mm aa");
                convertedDate = simpleDateFormat.format(dateFormat);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            LocationHistory history = new LocationHistory();
            history.setTimeStamp(convertedDate);
            history.setLocationEntityList(locationEntities);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("visited");
            reference.child(FirebaseAuth.getInstance().getUid()).
                    child(convertedDate).setValue(locationEntities);

            LocationDatabase.getInstance(getApplication()).historyDao().insertLocationHistory(history);
            LocationDatabase.getInstance(getApplication()).locationDao().deleteAll();
        }
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TAG, "onTaskRemoved: ");
        stopSelf();
    }

    private void removeData() {
        Log.i(TAG, "removeData: ");
        AsyncTask.execute(new InsertDataRunnable());
        unregisterReceiver(connectionReceiver);
        fusedLocationProvider.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        removeData();
        super.onDestroy();
    }
}
