package com.example.micke.lions.indoor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.micke.lions.DataSetChanged;
import com.example.micke.lions.R;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */

public class IndoorListFragment extends Fragment implements DataSetChanged, SearchView.OnQueryTextListener {

    String ILF = "IndoorListFragment";

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    //ip stands for interest points.
    private RecyclerView ipRecyclerView;

    private RecyclerView.LayoutManager ipLayoutManager;
    private String buildingId;
    private String filterText;
    public List<PointOfInterest> myDataset;
    public ipAdapter ipadapter;

    public IndoorListFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static IndoorListFragment newInstance(int sectionNumber) {
        IndoorListFragment fragment = new IndoorListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String buildingId = ((IndoorActivity) getActivity()).buildingId;
        setHasOptionsMenu(true);

        myDataset = ((IndoorActivity) getActivity()).getFireBaseHandler().getPoints(buildingId, this, false);
        ipadapter = new ipAdapter(getContext(), myDataset);

        View rootView = inflater.inflate(R.layout.fragment_indoor_list, container, false);

        ipRecyclerView = (RecyclerView) rootView.findViewById(R.id.ip_recycler_view);
        ipRecyclerView.setHasFixedSize(true);
        ipLayoutManager = new LinearLayoutManager(getActivity());
        ipRecyclerView.setLayoutManager(ipLayoutManager);

        ipRecyclerView.setAdapter(ipadapter);


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_indoor_list, menu);

        final MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public void dataSetChanged() {
        ipadapter.notifyDataSetChanged();
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
        myDataset = ((IndoorActivity) getActivity()).getFireBaseHandler().getPoints(buildingId, this, true);

        return true;
    }

    public void filterTextFunction(String text) {
        final List<PointOfInterest> filteredDataset = filter(((IndoorActivity) getActivity()).myDataset, text);
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
}