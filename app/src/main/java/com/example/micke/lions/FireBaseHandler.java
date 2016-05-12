package com.example.micke.lions;

import android.content.Context;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 05/04/2016.
 */
public class FireBaseHandler {

    protected Firebase myFirebaseRef;

    public FireBaseHandler(Context context) {
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

    //Gets all admin accounts to compare
    void getAdminAccount(final LoginDialogFragment loginDialogFragment) {
        final List<String[]> res = new ArrayList<>();
        myFirebaseRef.child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String[] acc = new String[2];
                for(DataSnapshot account : snapshot.getChildren()) {
                    acc[0] = account.child("username").getValue().toString();
                    acc[1] = account.child("password").getValue().toString();
                    res.add(acc);
                }
                loginDialogFragment.setAccounts(res);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    /*
    * Generates a unique id in the firebase database. Used for buildings and points of interest.
    * Returns the unique id.
     */
    public String generateId() {
        Firebase temp = myFirebaseRef.push();
        Log.d("fb:id", temp.getKey().toString());
        return temp.getKey().toString();
    }
}
