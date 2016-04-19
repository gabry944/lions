package com.example.micke.lions.outdoor;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Administrator on 05/04/2016.
 */
public interface BuildingDataSetChanged {

    void dataSetChanged(List<Building> list);
    void panToMarker(LatLng point);

}
