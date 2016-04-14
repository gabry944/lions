package com.example.micke.lions;

import android.content.Context;
import android.util.Log;

import com.example.micke.lions.outdoor.Building;
import com.example.micke.lions.outdoor.BuildingDataSetChanged;
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
public class FireBaseOutdoor extends FireBaseHandler implements Serializable {

    FireBaseOutdoor(Context context) {
        super(context);
    }

    /*
     * Updates or adds a building.
     */
    public void updateBuilding(Building building) {
        Firebase buildingRef =
                myFirebaseRef.child("building/" + building.getId());
        buildingRef.setValue(building);
    }

    public void buildingListener(final BuildingDataSetChanged buildingDataSetChanged) {
        final List<Building> list = new ArrayList<>();
        myFirebaseRef.child("building").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot buildings) {
                list.clear();
                for (DataSnapshot building : buildings.getChildren()) {
                    Building b = new Building(building.getValue(Building.class));
                    Log.d("loadall", b.getLatitude() + " " + b.getLongitude() + " " + b.getId() + " " + b.getName());
                    Log.d("loadall", building.toString());
                    list.add(b);
                }
                buildingDataSetChanged.dataSetChanged(list);
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });
    }

    public List<Building> getBuildings(final DataSetChanged outdoorActivity) {
        final List<Building> list = new ArrayList<>();

        myFirebaseRef.child("building").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot buildings) {
                list.clear();
                Log.d("com/example/micke/lions/outdoor", "data changed");
                for (DataSnapshot building : buildings.getChildren()) {
                    Log.d("outdoorb", building.toString());
                    Building b = new Building(building.getValue(Building.class));
                    list.add(b);
                }
                outdoorActivity.dataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

        return list;
    }

}
