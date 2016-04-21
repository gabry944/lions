package com.example.micke.lions.indoor;

import android.content.Context;
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

    public FireBaseIndoor(Context context, String buildingId) {
        super(context);
        this.buildingId = buildingId;
    }

    /*
     * Updates or adds a point of interest.
     */
    public void updateIp(PointOfInterest point, int floor) {
        Firebase ipRef =
                myFirebaseRef.child("building/" + buildingId + "/floor/" + floor + "/ip/" + point.getId());
        ipRef.setValue(point);
    }

    public List<PointOfInterest> getPoints(String buildingId, final DataSetChanged indoorActivity, final boolean search) {
        final List<PointOfInterest> list = new ArrayList<>();

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

    public List<String> getFloors(String buildingId, final DataSetChanged indoorActivity) {
        final List<String> list = new ArrayList<>();

        myFirebaseRef.child("building/" + buildingId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot building) {
                list.clear();
                DataSnapshot floors = building.child("floor");
                Log.d("map", "data changed karta");
                for (DataSnapshot floor : floors.getChildren()) {
                    list.add(floor.getKey().toString());
                }
                indoorActivity.dataSetChanged();

            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

        return list;
    }

}
