package com.example.micke.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IndoorMapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link IndoorMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IndoorMapFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_ITEM_TITLE = "item_title";
    public static final String ARG_ITEM_CATEGORY = "item_category";
    public static final String ARG_ITEM_DESCRIPTION = "item_description";
    public static final String ARG_ITEM_LATITUDE = "item_latitude";
    public static final String ARG_ITEM_LONGITUDE = "item_longitude";
    public static final String ARG_ITEM_FLOOR = "item_floor";

    public IndoorMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_indoor_map, container, false);
    }

}
