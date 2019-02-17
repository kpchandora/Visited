package developer.code.kpchandora.locationassignment.ui.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import developer.code.kpchandora.locationassignment.R;
import developer.code.kpchandora.locationassignment.roomdb.database.LocationDatabase;
import developer.code.kpchandora.locationassignment.roomdb.entities.LocationEntity;
import developer.code.kpchandora.locationassignment.roomdb.entities.LocationHistory;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback {


    public static final String MAPS_TAG = "maps_tag";
    private GoogleMap mGoogleMap;
    private List<LatLng> entities;

    public static MapsFragment newInstance(LocationHistory history) {
        MapsFragment mapsFragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putSerializable(MAPS_TAG, history);
        mapsFragment.setArguments(args);
        return mapsFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        entities = new ArrayList<>();
        if (getArguments() == null) {
            return;
        }
        LocationHistory history = (LocationHistory) getArguments().getSerializable(MAPS_TAG);
        if (history != null) {
            for (LocationEntity entity : history.getLocationEntityList()) {
                entities.add(new LatLng(entity.getLat(), entity.getLng()));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (entities != null) {
            PolylineOptions options = new PolylineOptions();
            options.addAll(entities);
            mGoogleMap.addPolyline(options);
        }
    }
}
