package developer.code.kpchandora.locationassignment;

import android.Manifest;
import android.app.ActivityManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationProvider;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;

import java.util.List;

import developer.code.kpchandora.locationassignment.adapter.LocationAdapter;
import developer.code.kpchandora.locationassignment.roomdb.entities.LocationEntity;
import developer.code.kpchandora.locationassignment.service.LocationService;
import developer.code.kpchandora.locationassignment.service.MyJobService;
import developer.code.kpchandora.locationassignment.viewmodel.LocationViewModel;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 101;
    public final static String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private FirebaseJobDispatcher jobDispatcher;

    private LocationViewModel viewModel;
    private RecyclerView locationRecyclerView;
    private Button startButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationRecyclerView = findViewById(R.id.recyclerView);
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
                adapter.setLocation(locationEntities);
            }
        });

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
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
                    startButton.setText("Stop");
                    startService(new Intent(MainActivity.this, LocationService.class));
                } else {
                    startButton.setText("Start");
                    stopService(new Intent(MainActivity.this, LocationService.class));
                }
            }
        });
    }
}
