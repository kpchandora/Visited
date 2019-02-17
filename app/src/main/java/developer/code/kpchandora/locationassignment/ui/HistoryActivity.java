package developer.code.kpchandora.locationassignment.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import java.util.List;

import developer.code.kpchandora.locationassignment.R;
import developer.code.kpchandora.locationassignment.RootAnimActivity;
import developer.code.kpchandora.locationassignment.adapter.HistoryAdapter;
import developer.code.kpchandora.locationassignment.roomdb.entities.LocationHistory;
import developer.code.kpchandora.locationassignment.viewmodel.HistoryViewModel;

public class HistoryActivity extends RootAnimActivity implements HistoryAdapter.OnHistoryClickListener {

    private static final String TAG = "HistoryActivity";
    public static final String TIME_STAMP = "timeStamp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        HistoryViewModel historyViewModel = ViewModelProviders.of(this).get(HistoryViewModel.class);
        RecyclerView historyRecyclerView = findViewById(R.id.history_recycler_view);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final HistoryAdapter adapter = new HistoryAdapter(this);
        historyRecyclerView.setAdapter(adapter);

        historyViewModel.getHistoryLiveData().observe(this, new Observer<List<LocationHistory>>() {
            @Override
            public void onChanged(@Nullable List<LocationHistory> locationHistories) {
                adapter.setHistoryList(locationHistories);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onHistoryClick(String timeStamp) {
        Intent intent = new Intent(HistoryActivity.this, LocationsActivity.class);
        intent.putExtra(TIME_STAMP, timeStamp);
        startActivity(intent);
    }
}
