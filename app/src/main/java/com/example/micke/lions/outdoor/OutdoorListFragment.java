package com.example.micke.lions.outdoor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private BuildingAdapter buildingAdapter;
    private List<Building> myDataset;

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
        View rootView = inflater.inflate(R.layout.fragment_outdoor_list, container, false);

        myDataset = ((OutdoorActivity) getActivity()).getFireBaseHandler().getBuildings(this);

        buildingAdapter = new BuildingAdapter(getContext(), myDataset);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.building_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(buildingAdapter);

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
