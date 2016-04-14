package com.example.micke.myapplication;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentPagerAdapter;
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
import android.widget.Adapter;
import android.widget.SearchView;

import java.util.List;


public class IndoorActivity extends AppCompatActivity implements DataSetChanged, SearchView.OnQueryTextListener {

    private FireBaseIndoor fireBaseHandler;
    private IndoorPageSliderAdapter mSectionsPagerAdapter;
    public static ViewPager mViewPager;
    private RecyclerView ipRecyclerView;
    private ipAdapter ipadapter;
    private RecyclerView.LayoutManager ipLayoutManager;
    private List<PointOfInterest> myDataset;
    private String buildingId;
    private String ipId;

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
        mSectionsPagerAdapter = new IndoorPageSliderAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);

        myDataset = fireBaseHandler.getPoints(buildingId, this);

        // specify an adapter
        ipadapter = new ipAdapter(myDataset);
        //ipRecyclerView.setAdapter(ipadapter);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_options, menu);

        final MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // return super.onOptionsItemSelected(item);

        int id = item.getItemId();
        Log.d("ItemClicked", "Item: " + item.toString());
        if (id == R.id.item_add) {
            AddPointDialogFragment newFragment = new AddPointDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("firebase", fireBaseHandler);
            newFragment.setArguments(bundle);
            newFragment.show(this.getFragmentManager(), "add_point_layout");
        } else if (id == R.id.item_camera) {
            Intent intent = new Intent(getApplicationContext(), QRFragment.class);
            startActivity(intent);
        }
        return false;
    }

    @Override
    public void dataSetChanged() {
        ipadapter.notifyDataSetChanged();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d("text", newText);
        return false;
    }

    public ipAdapter getAdapter(){
        return ipadapter;
    }
}
