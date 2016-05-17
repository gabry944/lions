package com.example.micke.lions.outdoor;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.micke.lions.Common;
import com.example.micke.lions.R;
import com.example.micke.lions.indoor.IndoorActivity;
import com.example.micke.lions.LoginDialogFragment;

public class OutdoorActivity extends AppCompatActivity {

    private String TAG = "OutdoorActivity";

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    private static final int MY_PERMISSIONS_REQUEST_MAP = 1;

    public MenuItem adminButton;

    private OutdoorPageSliderAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private FireBaseOutdoor fireBaseHandler;

    public OutdoorMapFragment map;
    public OutdoorListFragment list;
    public OutdoorQRFragment qr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Let firebase start loading its data before anything else
        fireBaseHandler = new FireBaseOutdoor(getApplicationContext());

        //check for permission to use the camera
        //needed for the QRFragment
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }

        //check for permission to use the location
        //needed for the MapFragment
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_MAP);
        }

        setContentView(R.layout.activity_outdoor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("LIONS");

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new OutdoorPageSliderAdapter(getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);

        DialogFragment newFragment = new InfoDialogFragment();
        newFragment.show(getFragmentManager(), "info_dialog");

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        invalidateOptionsMenu();
    }

    public FireBaseOutdoor getFireBaseHandler() {
        return fireBaseHandler;
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    @Override
    public void onBackPressed()
    {
        if(mViewPager.getCurrentItem() != 1)
            mViewPager.setCurrentItem(1);
        else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_outdoor_activity, menu);
        adminButton = menu.findItem(R.id.admin);
        Common.setAdminButton(adminButton, this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id ==  R.id.admin){
            if (Common.IsAdmin()) {
                Common.LogOut(map, list, qr);
                Common.setAdminButton(adminButton, this);
            }
            else {
                LoginDialogFragment login = new LoginDialogFragment();
                login.show(getFragmentManager(), "login_fragment");
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_MAP: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Common.LocationPermissionDenied();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String[] parts = data.getStringArrayExtra("data");
                //parts[1] = building id
                //parts[2] = floors
                //parts[3] = floor id (1,2,3 etc.)
                //parts[4] = ips
                //parts[5] = ip id

                if(parts[0].equals("building")) {
                    //Start a new IndoorActivity for this o to map fragment
                    goToIP(parts, data.getStringExtra("goalID"), data.getStringExtra("goalFloor"));
                } else if(parts[0].equals("car")) {
                    //send to OutdoorQRFragment
                    mViewPager.setCurrentItem(2);
                    getCar(parts);
                }
            }
        }
    }

    public void getCar(String[] parts) {
        fireBaseHandler.getCar(qr, parts[1]);
    }

    public void goToIP(String[] parts, String goalID, String goalFloor) {
        //Go to map fragment
        Intent intent = new Intent(this, IndoorActivity.class);
        Bundle bundle = new Bundle();
        String buildingId = parts[1];
        String ipId = "-1";
        String floor = "";
        if (parts[5] != null) {
            ipId = parts[5];
            floor = parts[3];
        }
        bundle.putString("buildingId", buildingId);
        bundle.putString("ipId", ipId);
        bundle.putString("floor", floor);
        bundle.putString("goalID", goalID);
        bundle.putString("goalFloor", goalFloor);
        intent.putExtras(bundle);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //not allowed togeter with startActivityForResult
        startActivityForResult(intent, 1);
    }

    public void goToIP(String[] parts) {
        //Go to map fragment
        Intent intent = new Intent(this, IndoorActivity.class);
        Bundle bundle = new Bundle();
        String buildingId = parts[1];
        String ipId = "-1";
        String floor = "";
        String currentBuilding = "";
        if (parts[5] != null) {
            ipId = parts[5];
            floor = parts[3];
        }
        if(parts[6] != null){
           currentBuilding = parts[6];
        }
        bundle.putString("buildingId", buildingId);
        bundle.putString("ipId", ipId);
        bundle.putString("floor", floor);
        bundle.putString("buildingTitle", currentBuilding);
        intent.putExtras(bundle);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //not allowed togeter with startActivityForResult
        startActivityForResult(intent, 1);
    }
}
