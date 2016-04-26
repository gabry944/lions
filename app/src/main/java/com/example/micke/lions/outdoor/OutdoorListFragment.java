package com.example.micke.lions.outdoor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.micke.lions.DataSetChanged;
import com.example.micke.lions.R;
import com.example.micke.lions.indoor.IndoorActivity;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class OutdoorListFragment extends Fragment implements DataSetChanged {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private OutdoorActivity outdoorActivity;
    private BuildingAdapter buildingAdapter;
    private List<Building> myDataset;

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

        goToMaps = (ImageButton) rootView.findViewById(R.id.goToMaps);
        goToQR = (ImageButton) rootView.findViewById(R.id.goToQr);

        myDataset = outdoorActivity.getFireBaseHandler().getBuildings(this);

        buildingAdapter = new BuildingAdapter(getContext(), myDataset);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.building_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(outdoorActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(buildingAdapter);

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
    public void dataSetChanged() {
        buildingAdapter.notifyDataSetChanged();
    }

    @Override
    public void fetchDataDone() {

    }
}
