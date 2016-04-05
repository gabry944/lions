package com.example.micke.myapplication;

import android.content.Context;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import android.content.Context;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by Administrator on 05/04/2016.
 */
public class FireBaseHandler {

    private Firebase myFirebaseRef;

    FireBaseHandler(Context context) {
        Firebase.setAndroidContext(context);
        myFirebaseRef = new Firebase("https://torrid-inferno-7041.firebaseio.com/");
    }

    public void test() {
        myFirebaseRef.child("building/1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot building) {

                DataSnapshot floors = building.child("floor");
                Log.d("hej", "data changed");
                for (DataSnapshot floor : floors.getChildren()) {
                    DataSnapshot ips = floor.child("ip");
                    for (DataSnapshot ip : ips.getChildren()) {
                        Log.d("ip", ip.getValue().toString());
                    }
                }

                //prints "Do you have data? You'll love Firebase."
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });
    }
}
