package com.example.micke.lions.indoor;

import android.app.DialogFragment;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.example.micke.lions.DataSetChanged;
import com.example.micke.lions.outdoor.OutdoorQRFragment;
import com.example.micke.lions.R;

import java.util.ArrayList;
import java.util.List;


public class IndoorActivity extends AppCompatActivity implements DataSetChanged, SearchView.OnQueryTextListener {

    private FireBaseIndoor fireBaseHandler;
    private IndoorPageSliderAdapter mSectionsPagerAdapter;
    public static ViewPager mViewPager;
    private RecyclerView ipRecyclerView;
    private ipAdapter ipadapter;
    private RecyclerView.LayoutManager ipLayoutManager;
    private List<PointOfInterest> myDataset;
    private List<String> mFloors;
    private String buildingId;
    private String ipId;
    public IndoorMapFragment map;
    public IndoorListFragment list;
    public IndoorQRFragment qr;
    public FloorAdapter floorAdapter;
    private String filterText;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_indoor);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        buildingId = bundle.getString("buildingId", "1");
        ipId = bundle.getString("ipId", "-1");

        fireBaseHandler = new FireBaseIndoor(getApplicationContext(), buildingId);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new IndoorPageSliderAdapter(getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);

        myDataset = fireBaseHandler.getPoints(buildingId, this, false);
        mFloors = fireBaseHandler.getFloors(buildingId, this, false);

        ipadapter = new ipAdapter(this, myDataset);
        floorAdapter = new FloorAdapter(this, mFloors);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new AddPointDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("firebase", fireBaseHandler);
                newFragment.setArguments(bundle);
                newFragment.show(getFragmentManager(), "add_point_layout");
//                Snackbar.make(view, "BLablabla", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        //If QRFragment gets user here, go to indoor map
        if(!ipId.equals("-1")) {
            mViewPager.setCurrentItem(0);
        }
    }

    @Override
    public void dataSetChanged() {
        ipadapter.notifyDataSetChanged();
        floorAdapter.notifyDataSetChanged();
    }

    @Override
    public void fetchDataDone() {
        filterTextFunction(filterText);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        filterText = newText;
        myDataset = fireBaseHandler.getPoints(buildingId, this, true);

        return true;
    }

    public void filterTextFunction(String text) {
        final List<PointOfInterest> filteredDataset = filter(myDataset, text);
        ipadapter.updateAdapter(filteredDataset);
    }

    private List<PointOfInterest> filter(List<PointOfInterest> myDataset, String query){
        query = query.toLowerCase();
        final List<PointOfInterest> filteredDataset = new ArrayList<>();

        for(PointOfInterest ip: myDataset){
            final String text = ip.getTitle().toLowerCase();
            if(text.contains(query)){
                filteredDataset.add(ip);
            }
        }
        return  filteredDataset;

    }

    public ipAdapter getAdapter(){ return ipadapter; }
    public List<PointOfInterest> getData() { return myDataset; }
    public FireBaseIndoor getFireBaseHandler() { return fireBaseHandler; }

    @Override
    public void onBackPressed()
    {
        if(mViewPager.getCurrentItem() != 1)
            mViewPager.setCurrentItem(1);
        else
            super.onBackPressed();
    }
}