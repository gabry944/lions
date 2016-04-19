package com.example.micke.lions.indoor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.micke.lions.R;


/**
 * A placeholder fragment containing a simple view.
 */

public class ShowFloorsFragment extends Fragment {

    String ILF = "IndoorListFragment";

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    //ip stands for interest points.
    private RecyclerView ipRecyclerView;
    private RecyclerView.LayoutManager ipLayoutManager;

    public ShowFloorsFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ShowFloorsFragment newInstance(int sectionNumber) {
        ShowFloorsFragment fragment = new ShowFloorsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String buildingId = "1";

        View rootView = inflater.inflate(R.layout.fragment_show_floors, container, false);

        ipRecyclerView = (RecyclerView) rootView.findViewById(R.id.ip_recycler_view);
        ipRecyclerView.setHasFixedSize(true);
        ipLayoutManager = new LinearLayoutManager(getActivity());
        ipRecyclerView.setLayoutManager(ipLayoutManager);

        ipRecyclerView.setAdapter(((IndoorActivity) getActivity()).floorAdapter);

        return rootView;
    }

}