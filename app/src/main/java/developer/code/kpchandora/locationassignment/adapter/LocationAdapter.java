package developer.code.kpchandora.locationassignment.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import developer.code.kpchandora.locationassignment.R;
import developer.code.kpchandora.locationassignment.roomdb.entities.LocationEntity;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.MyHolder> {

    private final LayoutInflater inflater;
    private List<LocationEntity> locationList;
    private Context context;

    public LocationAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_layout, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

        final LocationEntity currentLocation = locationList.get(position);

        if (currentLocation.getAddress().equalsIgnoreCase("NA")){
            holder.locationRelativeLayout.setBackgroundResource(R.drawable.unknown_address_icon);
        }else {
            holder.locationRelativeLayout.setBackgroundResource(R.drawable.location_icon);
        }

        String lat = String.valueOf(currentLocation.getLat());
        String lng = String.valueOf(currentLocation.getLng());
        if (lat.length() > 9) {
            lat = lat.substring(0, 9);
        }
        if (lng.length() > 9) {
            lng = lng.substring(0, 9);
        }
        holder.coordinatesTextView.setText("Lat: " + lat + "    " + "Lng: " + lng);
        if (currentLocation.getAddress() == null) {
            holder.addressTextView.setVisibility(View.GONE);
        } else {
            holder.addressTextView.setText(currentLocation.getAddress());
        }
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
        private RelativeLayout locationRelativeLayout;

        public MyHolder(View itemView) {
            super(itemView);
            addressTextView = itemView.findViewById(R.id.address_textView);
            coordinatesTextView = itemView.findViewById(R.id.coordinates_textView);
            locationRelativeLayout = itemView.findViewById(R.id.location_relative_layout);
        }
    }
}

