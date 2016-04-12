package com.example.micke.myapplication;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by iSirux on 2016-04-11.
 */
public class FireBaseIndoor extends FireBaseHandler implements Serializable {

    private String buildingId;

    FireBaseIndoor(Context context, String buildingId) {
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

    public List<PointOfInterest> getPoints(String buildingId, final IndoorActivity indoorActivity) {
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
                indoorActivity.dataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

        return list;
    }

}