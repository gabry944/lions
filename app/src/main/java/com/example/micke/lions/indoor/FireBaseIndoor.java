package com.example.micke.lions.indoor;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.micke.lions.DataSetChanged;
import com.example.micke.lions.FireBaseHandler;
import com.example.micke.lions.indoor.PointOfInterest;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FireBaseIndoor extends FireBaseHandler implements Serializable {

    private String buildingId;
    private String floorId;

    public FireBaseIndoor(Context context, String buildingId) {
        super(context);
        this.buildingId = buildingId;
        floorId = "4";
    }

    /*
     * Updates or adds a point of interest.
     */
    public void updateIp(PointOfInterest point, int floor) {
        Firebase ipRef =
                myFirebaseRef.child("building/" + buildingId + "/floor/" + floor + "/ip/" + point.getId());
        ipRef.setValue(point);
    }

    //Used by list fragment
    public List<PointOfInterest> getPoints(String buildingId, final DataSetChanged indoorActivity, final boolean search) {
        final List<PointOfInterest> list = new ArrayList<>();
        Log.d("indoor", "getting points for " +  buildingId);

        myFirebaseRef.child("building/" + buildingId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot building) {
                list.clear();
                DataSnapshot floors = building.child("floor");
                Log.d("hej", "data changed");
                for (DataSnapshot floor : floors.getChildren()) {
                    DataSnapshot ips = floor.child("ip");
                    for (DataSnapshot ip : ips.getChildren()) {
                        Log.d("ip", ip.getValue().toString());
                        PointOfInterest point = new PointOfInterest(ip.getValue(PointOfInterest.class));
                        list.add(point);
                    }
                }
                if (search)
                    indoorActivity.fetchDataDone();
                else
                    indoorActivity.dataSetChanged();

            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

        return list;
    }

    //Used by map fragment
    public List<PointOfInterest> getPoints(String buildingId, final IndoorMapMarkerChange indoorMapFragment) {
        final List<PointOfInterest> list = new ArrayList<>();
        Log.d("indoor", "getting points for " +  buildingId);

        myFirebaseRef.child("building/" + buildingId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot building) {
                list.clear();
                Log.d("hej", "data changed");
                Log.d("floor", "getting points for floor " + floorId);
                DataSnapshot floors = building.child("floor");
                for (DataSnapshot floor : floors.getChildren()) {
                    DataSnapshot ips = floor.child("ip");
                    for (DataSnapshot ip : ips.getChildren()) {
                        Log.d("ip", ip.getValue().toString());
                        PointOfInterest point = new PointOfInterest(ip.getValue(PointOfInterest.class));
                        list.add(point);
                    }
                }
                indoorMapFragment.getUpdatedDataSet(list);
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

        return list;
    }

    //Used by floor menu in the map
    public List<String> getFloors(String buildingId, final IndoorMapMarkerChange indoorMapMarkerChange) {
        final List<String> list = new ArrayList<>();

        myFirebaseRef.child("building/" + buildingId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot building) {
                list.clear();
                DataSnapshot floors = building.child("floor");

                for (DataSnapshot floor : floors.getChildren()) {
                    list.add(floor.getKey().toString());
                }
                indoorMapMarkerChange.dataSetChanged();

            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

        return list;
    }

    public void setFloor(String f) {
        floorId = f;
    }
    public String getFloor() {
        return floorId;
    }
}
