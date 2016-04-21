package com.example.micke.lions.outdoor;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.micke.lions.indoor.IndoorActivity;
import com.example.micke.lions.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
    private List<Marker> carMarkerList;
    private OutdoorActivity outdoorActivity;

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
        outdoorActivity = (OutdoorActivity) getActivity();

        try {
            rootView = inflater.inflate(R.layout.activity_outdoor_maps, container, false);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
            return rootView;
        }

        carMarkerList = new ArrayList<>();

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

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { return; }
        mMap.setMyLocationEnabled(true);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,
                longitude), 20.0f));

        mMap.setOnMapLongClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);

        buildings = new ArrayList<>();
        outdoorActivity.getFireBaseHandler().buildingListener(this);
    }

    public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map);
            if(mapFragment != null) {
                mapFragment.getMapAsync(this);
            } else
                Log.e("map", "mapFragment = null");
        }
    }

    @Override
    public void onMapLongClick(LatLng point) {
        DialogFragment newFragment = new AddBuildingDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("latlng", point);
        bundle.putSerializable("mapfragment", this);
        newFragment.setArguments(bundle);
        newFragment.show(outdoorActivity.getFragmentManager(), "add_building_layout");
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

    public void newMarker(Car car, Location location) {
        LatLng point = new LatLng(car.getLatitude(), car.getLongitude());

        //Hide all other parked cars
        carMarkerList.clear();
        //Clear the map, clears all markers and other stuff
        mMap.clear();

        Marker carMarker = mMap.addMarker(new MarkerOptions()
                .position(point)
                .title(car.getName())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        carMarkerList.add(carMarker);

//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//        builder.include(carMarker.getPosition());
//        builder.include(new LatLng(location.getLatitude(), location.getLongitude()));

//        LatLngBounds bounds = builder.build();
//        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(car.getLatitude(), car.getLongitude()), 19));
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

    @Override
    public void panToMarker(LatLng point) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 18));
    }
}
