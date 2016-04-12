package com.example.micke.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A placeholder fragment containing a simple view.
 */
public class OutdoorMapFragment extends Fragment implements OnMapReadyCallback {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private GoogleMap mMap;
    private double longitude, latitude;
    private View rootView;

    public OutdoorMapFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static OutdoorMapFragment newInstance(int sectionNumber) {
        OutdoorMapFragment fragment = new OutdoorMapFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            rootView = inflater.inflate(R.layout.activity_outdoor_maps, container, false);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
            return rootView;
        }

        Log.d("map", "OutdoorKartFragment created");

        latitude = 58.3918064;
        longitude = 15.5654057;

        setUpMapIfNeeded();
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("map", "outdoorkartfragment - onmapready");
        mMap = googleMap;

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,
                longitude), 22.0f));
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                .title("Vita Huset").snippet("Det är här det händer"));
    }

    public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map);
            if(mapFragment != null)
                mapFragment.getMapAsync(this);
            else
                Log.e("map", "mapFragment = null");
        }
    }
}
