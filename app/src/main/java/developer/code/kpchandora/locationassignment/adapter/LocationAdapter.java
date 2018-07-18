package developer.code.kpchandora.locationassignment.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import developer.code.kpchandora.locationassignment.R;
import developer.code.kpchandora.locationassignment.roomdb.entities.LocationEntity;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.MyHolder> {

    private final LayoutInflater inflater;
    private List<LocationEntity> locationList;
    private LocationClickListener listener;
    private Context context;

    public LocationAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
//        this.listener = (LocationClickListener) context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_layout, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

        final LocationEntity currentLocation = locationList.get(position);

        String lat = String.valueOf(currentLocation.getLat());
        String lng = String.valueOf(currentLocation.getLng());
        if (lat.length() > 6) {
            lat = lat.substring(0, 6);
        }
        if (lng.length() > 6) {
            lng = lng.substring(0, 6);
        }
        holder.coordinatesTextView.setText("Lat: " + lat + "    " + "Lng: " + lng + "  id:" + currentLocation.getId());
        if (currentLocation.getAddress() == null) {
            holder.addressTextView.setVisibility(View.GONE);
        } else {
            holder.addressTextView.setText(currentLocation.getAddress());
        }
    }

    public interface LocationClickListener {
        void onPersonLongClick(LocationEntity locationEntity);

        void onPersonClick(LocationEntity locationEntity);
    }

    public void setLocation(List<LocationEntity> locationList) {
        this.locationList = locationList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (locationList != null)
            return locationList.size();
        return 0;
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        private TextView addressTextView;
        private TextView coordinatesTextView;

        public MyHolder(View itemView) {
            super(itemView);
            addressTextView = itemView.findViewById(R.id.address_textView);
            coordinatesTextView = itemView.findViewById(R.id.coordinates_textView);
        }
    }
}

