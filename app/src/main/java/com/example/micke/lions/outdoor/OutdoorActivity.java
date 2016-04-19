package com.example.micke.lions.outdoor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.micke.lions.DataSetChanged;
import com.example.micke.lions.indoor.IndoorActivity;
import com.example.micke.lions.R;

import java.util.List;

public class OutdoorActivity extends AppCompatActivity implements DataSetChanged {

    private OutdoorPageSliderAdapter mSectionsPagerAdapter;
    int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    private ViewPager mViewPager;
    private FireBaseOutdoor fireBaseHandler;
    private List<Building> myDataset;
    public OutdoorMapFragment map;
    public OutdoorListFragment list;
    public OutdoorQRFragment qr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outdoor);

        fireBaseHandler = new FireBaseOutdoor(getApplicationContext());

        myDataset = fireBaseHandler.getBuildings(this);

        //check for permission to use the camera
        //needed for the QRFragment
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            /*if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                Log.d("OutdoorActivity", "onCreate: should show request");
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {*/
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            //}
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new OutdoorPageSliderAdapter(getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), IndoorActivity.class);
                Bundle bundle = new Bundle();
                String buildingId = "1";
                bundle.putString("buildingId", buildingId);
                intent.putExtras(bundle);
                startActivity(intent);
//                Intent intent = new Intent(getApplicationContext(), IndoorActivity.class);
//                startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_outdoor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void dataSetChanged() {
        Log.d("com", "dataSetChanged com.example.micke.lions.outdoor");
    }

    @Override
    public void fetchDataDone() {

    }

    public FireBaseOutdoor getFireBaseHandler() {
        return fireBaseHandler;
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }
}
