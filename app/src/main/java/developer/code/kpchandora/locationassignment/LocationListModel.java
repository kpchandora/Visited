package developer.code.kpchandora.locationassignment;

import java.util.List;

import developer.code.kpchandora.locationassignment.roomdb.entities.LocationEntity;

public class LocationListModel {

    private List<LocationEntity> locationEntityList;

    public List<LocationEntity> getLocationEntityList() {
        return locationEntityList;
    }

    public void setLocationEntityList(List<LocationEntity> locationEntityList) {
        this.locationEntityList = locationEntityList;
    }
}
