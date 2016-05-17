package com.example.micke.lions.outdoor;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.micke.lions.Common;
import com.example.micke.lions.InloggChange;
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
        Serializable, GoogleMap.OnMarkerClickListener, BuildingDataSetChanged, InloggChange {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    private String TAG = "OutdoorMapFragment";

    private static final String ARG_SECTION_NUMBER = "section_number";
    private GoogleMap mMap;
    private double longitude, latitude;
    private View rootView;
    private List<Building> buildings;
    private List<Marker> carMarkerList;
    private OutdoorActivity outdoorActivity;
    private ImageButton goToList;

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

        goToList = (ImageButton) rootView.findViewById(R.id.goToOutdoorList1);
        goToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager mPager = (ViewPager) v.getRootView().findViewById(R.id.container);
                mPager.setCurrentItem(1, true);
            }
        });

        carMarkerList = new ArrayList<>();

        latitude = 58.3918064;
        longitude = 15.5654057;

        setUpMapIfNeeded();
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_outdoor_map, menu);
        MenuItem add = menu.findItem(R.id.addBuildingBtn);
        if(Common.IsAdmin())
            add.setVisible(true);
        else
            add.setVisible(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.addBuildingBtn) {
            Context context = getContext();
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, R.string.addMarkerExplanation, duration);
            //toast.setGravity(Gravity.TOP| Gravity.CENTER, 0, 150);
            toast.show();
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setMyLocationEnabled(true); //Needs a permission check

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
                Log.e(TAG, "mapFragment = null");
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

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(point)
                .title(building.getName())
                .snippet(building.getId())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        marker.showInfoWindow();
        Log.d("loadall", "creating new marker at: " + marker.getPosition());
    }

    public void newMarker(Car car, Location location) {
        LatLng point = new LatLng(car.getLatitude(), car.getLongitude());

        //Hide all other parked cars
        carMarkerList.clear();
        //Clear the map, clears all markers and other stuff
        mMap.clear();
        loadAllBuildings();

        Marker carMarker = mMap.addMarker(new MarkerOptions()
                .position(point)
                .title(car.getName())
                .snippet("car")
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

        String snippet = marker.getSnippet();
        //If it´s a car snippet == car, else it´s the building Id.
        if(!snippet.equals("car")){
            Intent intent = new Intent(getContext(), IndoorActivity.class);
            Bundle bundle = new Bundle();
            String currentBuilding = marker.getTitle();
            bundle.putString("buildingId", snippet);
            bundle.putString("buildingTitle", currentBuilding);
            intent.putExtras(bundle);
            getActivity().startActivityForResult(intent, 1);
        }
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

    @Override
    public void adminInlogg() {
        Log.d(TAG, "adminInlogg: ");
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void commonInlogg() {
        Log.d(TAG, "commonInlogg: ");
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onPrepareOptionsMenu (Menu menu) {
        MenuItem add = menu.findItem(R.id.addBuildingBtn);
        if (Common.IsAdmin())
            add.setVisible(true);
        else
            add.setVisible(false);
    }
}
