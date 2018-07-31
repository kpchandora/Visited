package developer.code.kpchandora.locationassignment;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import developer.code.kpchandora.locationassignment.adapter.LocationAdapter;
import developer.code.kpchandora.locationassignment.roomdb.entities.LocationEntity;
import developer.code.kpchandora.locationassignment.service.LocationService;
import developer.code.kpchandora.locationassignment.service.MyJobService;
import developer.code.kpchandora.locationassignment.viewmodel.LocationViewModel;

public class MainActivity extends RootAnimActivity {

    private static final String TAG = "MainActivity";

    private static final int PERMISSION_REQUEST_CODE = 101;
    private static final int REQUEST_CHECK_SETTINGS = 12;
    public final static String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private FirebaseJobDispatcher jobDispatcher;

    private LocationViewModel viewModel;
    private RecyclerView locationRecyclerView;
    private Button startButton;
    private ImageView emptyView;
    private TextView fetchingTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationRecyclerView = findViewById(R.id.recyclerView);
        emptyView = findViewById(R.id.emptyImageView);
        fetchingTextView = findViewById(R.id.emptyTextView);
        locationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        viewModel = ViewModelProviders.of(this).get(LocationViewModel.class);

        jobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        startButton = findViewById(R.id.locationButton);
        startButtonClick();
        if (!checkPermission()) {
            askPermission();
        }

        schedule();
        final LocationAdapter adapter = new LocationAdapter(this);
        locationRecyclerView.setAdapter(adapter);

        viewModel.getListLiveData().observe(this, new Observer<List<LocationEntity>>() {
            @Override
            public void onChanged(@Nullable List<LocationEntity> locationEntities) {
                if (locationEntities != null && locationEntities.size() > 0) {
                    emptyView.setVisibility(View.GONE);
                    fetchingTextView.setVisibility(View.GONE);
                } else {
                    emptyView.setVisibility(View.VISIBLE);
                }
                adapter.setLocation(locationEntities);
//                adapter.notifyItemInserted(0);
//                locationRecyclerView.scrollToPosition(0);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.history_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.history) {
            startActivity(new Intent(MainActivity.this, HistoryActivity.class));
        }
        if (item.getItemId() == R.id.sign_out) {
            signOutUser();
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOutUser() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        stopService(new Intent(MainActivity.this, LocationService.class));
        finish();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkPermission() {

        if (ContextCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MyApplication.getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void schedule() {
        Job job = jobDispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("unique_tag")
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .build();

        jobDispatcher.mustSchedule(job);
    }

    private void startButtonClick() {

        if (isMyServiceRunning(LocationService.class)) {
            startButton.setText("Stop");
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (startButton.getText().toString().equalsIgnoreCase("Start")) {
                    if (checkLocationSettings()) {
                        startButton.setText("Stop");
                        fetchingTextView.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                               MainActivity.this.startService(new Intent(MainActivity.this, LocationService.class));
                            }
                        }, 2000);

                    }
                } else {
                    startButton.setText("Start");
                    stopService(new Intent(MainActivity.this, LocationService.class));
                    startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                    fetchingTextView.setVisibility(View.GONE);
                }
            }
        });
    }

    private boolean checkLocationSettings() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager != null && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showLocationDialog();
            return false;
        }
        return true;
    }

    private void showLocationDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Location is not enabled");
        dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
            }
        });
        dialog.setNegativeButton("Exit", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                paramDialogInterface.dismiss();
            }
        });
        dialog.setCancelable(false);

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }
}
