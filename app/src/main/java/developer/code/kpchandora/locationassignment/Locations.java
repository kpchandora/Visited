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

        String[] addresses = history.getHistoryAddress().split(Utils.LAT_LNG_DELIMITER);

        String[] latLngString = history.getLocationsString().split(Utils.LAT_LNG_DELIMITER);
        String[] latArray = latLngString[0].split(Utils.LAT_DELIMITER);
        String[] lngArray = latLngString[1].split(Utils.LNG_DELIMITER);

        List<LocationEntity> entityList = new ArrayList<>();
        for (int i = 0; i < addresses.length; i++) {
            LocationEntity entity = new LocationEntity();
            entity.setAddress(addresses[i]);
            entity.setLat(Double.parseDouble(latArray[i]));
            entity.setLng(Double.parseDouble(lngArray[i]));
            entityList.add(entity);
        }

        adapter.setLocation(entityList);


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
