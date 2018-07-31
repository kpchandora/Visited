package developer.code.kpchandora.locationassignment.adapter;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import java.util.List;

import developer.code.kpchandora.locationassignment.R;
import developer.code.kpchandora.locationassignment.roomdb.entities.LocationHistory;
import developer.code.kpchandora.locationassignment.roomdb.utils.Utils;

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


        AlphaAnimation aa = new AlphaAnimation(0.1f, 1.0f);
        aa.setDuration(500);
        holder.itemView.startAnimation(aa);

        String[] timeStamp = history.getTimeStamp().split("GMT");
        holder.historyTextView.setText(timeStamp[0]);

        String[] addresses = history.getHistoryAddress().split(Utils.LAT_LNG_DELIMITER);

        holder.sourceTextView.setText("Source: "+addresses[0]);
        holder.destinationTextView.setText("Destination: " + addresses[addresses.length - 1]);
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
        private TextView sourceTextView;
        private TextView destinationTextView;

        public MyHolder(View itemView) {
            super(itemView);
            historyTextView = itemView.findViewById(R.id.time_text_view);
            sourceTextView = itemView.findViewById(R.id.source_textview);
            destinationTextView = itemView.findViewById(R.id.destination_textview);
        }
    }
}
