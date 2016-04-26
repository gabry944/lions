package com.example.micke.lions.outdoor;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.micke.lions.DataSetChanged;
import com.example.micke.lions.InloggChange;
import com.example.micke.lions.R;
import com.example.micke.lions.indoor.IndoorActivity;
import com.example.micke.lions.indoor.PointOfInterest;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class OutdoorListFragment extends Fragment implements DataSetChanged, SearchView.OnQueryTextListener, InloggChange {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    String TAG = "OutdoorListFragment";
    private static final String ARG_SECTION_NUMBER = "section_number";
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private OutdoorActivity outdoorActivity;
    private BuildingAdapter buildingAdapter;
    private List<Building> myDataset;
    private String filterText;

    private ImageButton goToQR;
    private ImageButton goToMaps;

    public OutdoorListFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static OutdoorListFragment newInstance(int sectionNumber) {
        OutdoorListFragment fragment = new OutdoorListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        outdoorActivity = (OutdoorActivity) getActivity();
        View rootView = inflater.inflate(R.layout.fragment_outdoor_list, container, false);

        myDataset = outdoorActivity.getFireBaseHandler().getBuildings(this, false);

        goToMaps = (ImageButton) rootView.findViewById(R.id.goToMaps);
        goToQR = (ImageButton) rootView.findViewById(R.id.goToQr);

        myDataset = outdoorActivity.getFireBaseHandler().getBuildings(this, false);

        buildingAdapter = new BuildingAdapter(getContext(), myDataset);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.building_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(outdoorActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(buildingAdapter);

        setHasOptionsMenu(true);

        //Goes to QR code scanner fragment when user clicks on button
        goToMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager mPager = (ViewPager) v.getRootView().findViewById(R.id.container);
                 mPager.setCurrentItem(0, true);
            }
        });

        //Goes to Maps fragment when user clicks on button
        goToQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager mPager = (ViewPager) v.getRootView().findViewById(R.id.container);
                mPager.setCurrentItem(2, true);
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_outdoor_list, menu);

        final MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.info:
                DialogFragment newFragment = new InfoDialogFragment();
                newFragment.show(getActivity().getFragmentManager(), "info_dialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void dataSetChanged() {
        buildingAdapter.notifyDataSetChanged();
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
        myDataset = outdoorActivity.getFireBaseHandler().getBuildings(this, true);
        return true;
    }

    public void filterTextFunction(String text) {
        final List<Building> filteredDataset = filter(myDataset, text);
        buildingAdapter.updateAdapter(filteredDataset);
    }

    private List<Building> filter(List<Building> myDataset, String query){
        query = query.toLowerCase();
        final List<Building> filteredDataset = new ArrayList<>();

        for(Building building: myDataset){
            if(building.getName() != null){
                final String text = building.getName().toLowerCase();
                if(text.contains(query)){
                    filteredDataset.add(building);
                }
            }
        }
        return  filteredDataset;
    }

    @Override
    public void adminInlogg() {
        Log.d(TAG, "adminInlogg: ");
    }

    @Override
    public void commonInlogg() {
        Log.d(TAG, "commonInlogg: ");
    }
}
