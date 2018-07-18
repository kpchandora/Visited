package developer.code.kpchandora.locationassignment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import developer.code.kpchandora.locationassignment.adapter.HistoryAdapter;
import developer.code.kpchandora.locationassignment.roomdb.entities.LocationHistory;
import developer.code.kpchandora.locationassignment.viewmodel.HistoryViewModel;

public class HistoryActivity extends AppCompatActivity implements HistoryAdapter.OnHistoryClickListener {

    private static final String TAG = "HistoryActivity";
    public static final String TIME_STAMP = "timeStamp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
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
    public void onHistoryClick(String timeStamp) {
        Intent intent = new Intent(HistoryActivity.this, Locations.class);
        intent.putExtra(TIME_STAMP, timeStamp);
        startActivity(intent);
    }
}
