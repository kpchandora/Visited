package developer.code.kpchandora.locationassignment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import developer.code.kpchandora.locationassignment.adapter.LocationAdapter;
import developer.code.kpchandora.locationassignment.roomdb.database.LocationDatabase;
import developer.code.kpchandora.locationassignment.roomdb.entities.LocationEntity;
import developer.code.kpchandora.locationassignment.roomdb.entities.LocationHistory;
import developer.code.kpchandora.locationassignment.roomdb.utils.Utils;

public class Locations extends RootAnimActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        RecyclerView locationsRecyclerView = findViewById(R.id.locations_recycler_view);
        locationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle bundle = getIntent().getExtras();
        String timeStamp = "";
        if (bundle != null) {
            timeStamp = bundle.getString(HistoryActivity.TIME_STAMP);
        }
        LocationAdapter adapter = new LocationAdapter(this);
        locationsRecyclerView.setAdapter(adapter);
        LocationHistory history = LocationDatabase.getInstance(getApplication()).historyDao().getSingleHistory(timeStamp);

        adapter.setLocation(history.getLocationEntityList());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
