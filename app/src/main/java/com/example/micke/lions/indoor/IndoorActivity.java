package com.example.micke.lions.indoor;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.micke.lions.Common;
import com.example.micke.lions.DataSetChanged;
import com.example.micke.lions.outdoor.Building;
import com.example.micke.lions.outdoor.BuildingAdapter;
import com.example.micke.lions.outdoor.OutdoorActivity;
import com.example.micke.lions.outdoor.OutdoorQRFragment;
import com.example.micke.lions.R;

import java.util.ArrayList;
import java.util.List;


public class IndoorActivity extends AppCompatActivity {

    private String TAG = "IndoorActivity";

    private FireBaseIndoor fireBaseHandler;
    private IndoorPageSliderAdapter mSectionsPagerAdapter;
    public static ViewPager mViewPager;

    private String ipId;
    private String currentBuilding;
    private String buildingId;
    public IndoorMapFragment map;
    public IndoorListFragment list;
    public IndoorQRFragment qr;
    public List<PointOfInterest> myDataset;
    public BuildingAdapter buildingAdapter;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_indoor);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        buildingId = bundle.getString("buildingId", "1");
        ipId = bundle.getString("ipId", "-1");
        currentBuilding = bundle.getString("buildingTitle");
        Log.d("indoor", "buldingId: " + buildingId);
        fireBaseHandler = new FireBaseIndoor(getApplicationContext(), buildingId);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(currentBuilding);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new IndoorPageSliderAdapter(getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);

        //If QRFragment gets user here, go to indoor map
        if(!ipId.equals("-1")) {
            mViewPager.setCurrentItem(0);
        }
    }

    public FireBaseIndoor getFireBaseHandler() { return fireBaseHandler; }

    public String getBuildingId() {
        return buildingId;
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
        inflater.inflate(R.menu.menu_indoor_activity, menu);
        MenuItem adminButton = menu.findItem(R.id.admin);
        Common.setAdminButton(adminButton, this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.admin) {
            if(Common.IsAdmin())
                Common.LogOut();
            else
                Common.MakeAdmin();
            MenuView.ItemView adminButton = (MenuView.ItemView) findViewById(R.id.admin);
            Common.setAdminButton(item, this);
        }
        return false;
    }
}
