package com.example.micke.lions.outdoor;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.micke.lions.indoor.IndoorActivity;
import com.example.micke.lions.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class OutdoorMapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, NewBuildingCallback,
        Serializable, GoogleMap.OnMarkerClickListener, BuildingDataSetChanged {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private GoogleMap mMap;
    private double longitude, latitude;
    private View rootView;
    private List<Building> buildings;

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

        mMap.setOnMapLongClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
//        mMap.setInfoWindowAdapter(new BuildingInfoWindowAdapter(getContext()));

        Log.d("loadall", "attempting to load buildings...");
        buildings = new ArrayList<Building>();
        ((OutdoorActivity) getActivity()).getFireBaseHandler().buildingListener(this);
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

    @Override
    public void onMapLongClick(LatLng point) {
        DialogFragment newFragment = new AddBuildingDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("firebase", ((OutdoorActivity) getActivity()).getFireBaseHandler());
        bundle.putParcelable("latlng", point);
        bundle.putSerializable("mapfragment", this);
        newFragment.setArguments(bundle);
        newFragment.show(getActivity().getFragmentManager(), "add_building_layout");
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    public void newMarker(Building building) {
        LatLng point = new LatLng(building.getLatitude(), building.getLongitude());
//        Log.d("loadall", "creating new marker at: " + building.getLatitude() + " " + building.getLongitude());
        mMap.addMarker(new MarkerOptions()
                .position(point)
                .title(building.getName())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    }

    public void loadAllBuildings() {
        for (Building building:buildings) {
            newMarker(building);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d("marker", "marker clicked");
        Intent intent = new Intent(getContext(), IndoorActivity.class);
        Bundle bundle = new Bundle();
        String buildingId = marker.getId();
        bundle.putString("buildingId", buildingId);
        intent.putExtras(bundle);
        startActivity(intent);
        return false;
    }

    @Override
    public void dataSetChanged(List<Building> list) {
        buildings = list;
        loadAllBuildings();
    }
}
