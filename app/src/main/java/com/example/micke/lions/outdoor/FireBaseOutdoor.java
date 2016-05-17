package com.example.micke.lions.outdoor;

import android.content.Context;
import android.util.Log;

import com.example.micke.lions.DataSetChanged;
import com.example.micke.lions.FireBaseHandler;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by iSirux on 2016-04-11.
 */
public class FireBaseOutdoor extends FireBaseHandler implements Serializable {

    public FireBaseOutdoor(Context context) {
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
                    Log.d("hejdata", building.child("latitude").getValue().toString());
                    Building b = new Building(
                            building.child(("name")).getValue().toString(),
                            building.child("id").getValue().toString(),
                            Double.parseDouble(building.child("latitude").getValue().toString()),
                            Double.parseDouble(building.child("longitude").getValue().toString())
                    );
                    list.add(b);
                }
                buildingDataSetChanged.dataSetChanged(list);
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });
    }

    public List<Building> getBuildings(final DataSetChanged dataSetChangedInterface, final boolean search) {
        final List<Building> list = new ArrayList<>();

        myFirebaseRef.child("building").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot buildings) {
                list.clear();
                Log.d("outdoor", "data changed");
                for (DataSnapshot building : buildings.getChildren()) {
                    Building b = new Building(building.getValue(Building.class));
                    list.add(b);
                }
                if (search)
                    dataSetChangedInterface.fetchDataDone();
                else
                    dataSetChangedInterface.dataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError error) {

            }
        });

        return list;
    }

    //New car or update car in to database
    public void updateCar(Car car) {
        Firebase carRef =
                myFirebaseRef.child("car/" + car.getId());
        carRef.setValue(car);
    }

    public void getCar(final FragmentResolver fragmentResolver, String carId) {

        myFirebaseRef.child("car/" + carId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("car", "done loading car");
                Car car = new Car(snapshot.getValue(Car.class));
                fragmentResolver.startCarDialog(car);
            }

            @Override
            public void onCancelled(FirebaseError error) {}
        });
    }

    public void goToBuilding(String id, final BuildingDataSetChanged map) {
        myFirebaseRef.child("building/" + id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Building building = new Building(snapshot.getValue(Building.class));
                if(map != null)
                    map.panToMarker(new LatLng(building.getLatitude(), building.getLongitude()));
            }

            @Override
            public void onCancelled(FirebaseError error) {}
        });
    }
}
