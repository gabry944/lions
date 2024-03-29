package com.example.micke.lions.indoor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.example.micke.lions.DataSetChanged;
import com.example.micke.lions.FireBaseHandler;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FireBaseIndoor extends FireBaseHandler implements Serializable {

    private String TAG = "FireBaseIndoor";

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

    //Only call first time a map needs to be uploaded to the server
    public void addMap(Bitmap bitmap, int floor) {
        Log.d(TAG, "addMap: start");
        Firebase imgRef = myFirebaseRef.child("buildingimages/" + buildingId + "/" + floor);
        imgRef.setValue(encodeThumbnail(bitmap));
        Log.d(TAG, "addMap: end");
    }

    public void removeIp(PointOfInterest point) {
        Firebase ipRef =
                myFirebaseRef.child("building/" + buildingId + "/floor/" + point.getFloor() + "/ip/" + point.getId());
        ipRef.removeValue();
    }

    //Used by list fragment
    public List<PointOfInterest> getPoints(String buildingId, final DataSetChanged dataSetChangedInterface, final boolean search) {
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

    //Loads bitmaps for the current building from firebase. Maps are special in that they are only
    //loaded once on starup. This means that if admin adds another map while a user is using the app,
    //the user needs to restart to see changes.
    public List<FloorMapImage> getMapimages(String buildingId, final IndoorMapMarkerChange indoorMapFragment) {
        final List<FloorMapImage> list = new ArrayList<>();

        myFirebaseRef.child("buildingimages/" + buildingId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot building) {
                for (DataSnapshot image : building.getChildren()) {
                    if(image.getValue() != null) {
                        Bitmap bitmap = decodeThumbnail(image.getValue().toString());
                        if(bitmap == null) Log.d("hejnull", "bitmap null!");
                        list.add(new FloorMapImage(bitmap, Integer.parseInt(image.getKey())));
                    }
                }
                indoorMapFragment.getMapimagesDataSet(list);
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
                DataSnapshot floors = building.child("floor");
                for (DataSnapshot floor : floors.getChildren()) {
                    DataSnapshot ips = floor.child("ip");
                    for (DataSnapshot ip : ips.getChildren()) {
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

    public void setFloor(String f) {
        floorId = f;
    }
    public String getFloor() {
        return floorId;
    }



    /**
     * @param bitmap
     * @return converting bitmap and return a string
     */
    public String encodeThumbnail(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
    }

    private Bitmap decodeThumbnail(String thumbData) {
        byte[] bytes = Base64.decode(thumbData, Base64.DEFAULT);
        Bitmap b = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        BitmapLoading.decodeSampledBitmapFromByteArray(bytes, bytes.length);

        return b;
    }
}
