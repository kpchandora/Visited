package developer.code.kpchandora.locationassignment.adapter;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import developer.code.kpchandora.locationassignment.R;
import developer.code.kpchandora.locationassignment.roomdb.entities.LocationHistory;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyHolder> {

    private List<LocationHistory> locationHistories;
    private Context context;
    private OnHistoryClickListener historyClickListener;

    public HistoryAdapter(Context context) {
        this.context = context;
        historyClickListener = (OnHistoryClickListener) context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_item_layout, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        final LocationHistory history = locationHistories.get(position);
        holder.historyTextView.setText(history.getTimeStamp());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                historyClickListener.onHistoryClick(history.getTimeStamp());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (locationHistories != null)
            return locationHistories.size();
        return 0;
    }

    public void setHistoryList(List<LocationHistory> locationHistories) {
        this.locationHistories = locationHistories;
        notifyDataSetChanged();
    }

    public interface OnHistoryClickListener {
        void onHistoryClick(String locationHistory);
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private TextView historyTextView;

        public MyHolder(View itemView) {
            super(itemView);
            historyTextView = itemView.findViewById(R.id.history_text_view);
        }
    }
}
