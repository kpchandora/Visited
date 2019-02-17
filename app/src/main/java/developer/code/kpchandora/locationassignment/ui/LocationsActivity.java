package developer.code.kpchandora.locationassignment.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import developer.code.kpchandora.locationassignment.R;
import developer.code.kpchandora.locationassignment.RootAnimActivity;
import developer.code.kpchandora.locationassignment.adapter.LocationAdapter;
import developer.code.kpchandora.locationassignment.roomdb.database.LocationDatabase;
import developer.code.kpchandora.locationassignment.roomdb.entities.LocationHistory;

public class LocationsActivity extends RootAnimActivity {

    private LocationHistory history;
    private RelativeLayout mapsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mapsContainer = findViewById(R.id.maps_container);
        RecyclerView locationsRecyclerView = findViewById(R.id.locations_recycler_view);
        locationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle bundle = getIntent().getExtras();
        String timeStamp = "";
        if (bundle != null) {
            timeStamp = bundle.getString(HistoryActivity.TIME_STAMP);
        }
        LocationAdapter adapter = new LocationAdapter(this);
        locationsRecyclerView.setAdapter(adapter);
        history = LocationDatabase.getInstance(getApplication()).historyDao().getSingleHistory(timeStamp);

        adapter.setLocation(history.getLocationEntityList());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.path_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.show_path:
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace()
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
