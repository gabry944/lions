package com.example.micke.lions.indoor;

import android.app.DialogFragment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.micke.lions.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class IndoorMapFragment extends Fragment implements IndoorMapMarkerChange {
    String TAG = "IndoorMapFragment";
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private RecyclerView mFloorRecyclerView;
    private RecyclerView.LayoutManager mFloorLayoutManager;

    private View rootView;
    private List<String> mFloors;
    public FloorAdapter floorAdapter;
    private FireBaseIndoor fireBaseIndoor;
    private String buildingId;
    private int currentFloor;
    private Context context;

    private int displayWidth;
    private int displayHeight;

    //Scaletest
    private MapImage mapImage;
    private BitmapLoading bitmapLoading;

    private List<PointOfInterest> pointList;

    private IndoorActivity indoorActivity;
    private List<IndoorMapMarker> listOfMarkers = new ArrayList<IndoorMapMarker>();

    private static final String ARG_SECTION_NUMBER = "section_number";

    public IndoorMapFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static IndoorMapFragment newInstance(int sectionNumber) {
        IndoorMapFragment fragment = new IndoorMapFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        indoorActivity = (IndoorActivity) getActivity();
        fireBaseIndoor = indoorActivity.getFireBaseHandler();
        context = getContext();

        buildingId = indoorActivity.getBuildingId();
        rootView = inflater.inflate(R.layout.activity_indoor_map, container, false);
        mFloors = fireBaseIndoor.getFloors(buildingId, this);
        floorAdapter = new FloorAdapter(this, mFloors);

        //Get display dimensions
        Display display = indoorActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayWidth = size.x;
        displayHeight = size.y;

        //Initialize bitmap loading class
        bitmapLoading = new BitmapLoading(context, displayWidth, displayHeight);

        //For list of floors
        mFloorRecyclerView = (RecyclerView) rootView.findViewById(R.id.floor_recycler_view);
        mFloorRecyclerView.setHasFixedSize(true);
        mFloorLayoutManager = new LinearLayoutManager(indoorActivity);
        mFloorRecyclerView.setLayoutManager(mFloorLayoutManager);

        mFloorRecyclerView.setAdapter(floorAdapter);

        //For the map
        final RelativeLayout r = (RelativeLayout) rootView.findViewById(R.id.mapLayout);
        r.setScaleX(1.0f);
        r.setScaleY(1.0f);

        //Get dimensions of r
        Log.d("point", "getting dimensions...");
        getDimensions(r);

        //mapImage
        mapImage = (MapImage) rootView.findViewById(R.id.scale_test);
        mapImage.setImage(new BitmapDrawable(getResources(), bitmapLoading.getFloorImage(R.drawable.map_t3)));
        mapImage.setParent(r);
        mapImage.setCallback(this);

        setHasOptionsMenu(true);

        pointList = fireBaseIndoor.getPoints(buildingId, this);

        return rootView;
    }

    public void highlightIP(String ipID) {
        Log.d(TAG, "highlightIP: piID = " + ipID);

        IndoorMapMarker start = null, end = null, elevator = null;

        for(IndoorMapMarker m : listOfMarkers) {
            //hide all except chosen ip and entrance
            if(m.getId().equals(ipID))
                end = m;
            else if(m.getCategory().equals(getString(R.string.Entrance)))
                start = m;
            else if(m.getCategory().equals(getString(R.string.Elevator)))
                elevator = m;
            else
                m.getMarker().setVisibility(View.GONE);
        }

        if(end != null) {
            if(start != null) {
                //if ipID floor != entrance floor
                /*if (!end.getPoint().getFloor().equals(start.getPoint().getFloor())) {
                    //show elevator
                    if (elevator != null) {
                        elevator.getMarker().setVisibility(View.VISIBLE);
                        start.getMarker().setVisibility(View.VISIBLE);
                        end.getMarker().setVisibility(View.GONE);
                    } else {
                        Log.d(TAG, "highlightIP: No elevator found");
                    }
                }*/
            }
            else
                Log.d(TAG, "highlightIP: Found no start/entrance");
        }
        else
            Log.d(TAG, "highlightIP: Found no IP with the gived ID");
    }

    public void showAddPointDialog(float[] point) {
        DialogFragment newFragment = new AddPointDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putFloat("lat", point[0]);
        bundle.putFloat("lng", point[1]);
        newFragment.setArguments(bundle);
        newFragment.show(indoorActivity.getFragmentManager(), "add_point_layout");
    }

    private void addPoint(RelativeLayout parent, PointOfInterest ip) {
        //TODO fixa vettiga värden
        final float[] point = mapImage.convertCoordinates(ip.getLatitude(), ip.getLongitude());

        IndoorMapMarker marker = new IndoorMapMarker(ip, point[0], point[1], parent.getContext());

        parent.addView(marker.getMarker());

        listOfMarkers.add(marker);

        if(ip.getCategory().toLowerCase().equals("hiss"))
                    addDescText(parent, ip.getCategory(), marker.getX(), marker.getY());

        marker.getMarker().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "Klickar på pungtjävel " + "posX = " + point[0] + " posY = " + point[1]);
            }
        });
    }

    private void addDescText(RelativeLayout parent, String category, float posX, float posY){
        TextView textView = new TextView(parent.getContext());
        textView.setText(category);
        textView.setTextSize(6);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        textView.setLayoutParams(layoutParams);
        textView.setX(posX);
        textView.setY(posY - 130);
        parent.addView(textView);
    }

    public void setCurrentFloor(String floor) {
        Log.d("floor", "" + floor);
        mapImage.resetView();
        fireBaseIndoor.setFloor(floor);
        if(floor.equals("3")) {
            currentFloor = 3;
            mapImage.setImage(new BitmapDrawable(getResources(), bitmapLoading.getFloorImage(R.drawable.map_t3)));
        }
        if(floor.equals("4")) {
            currentFloor = 4;
            mapImage.setImage(new BitmapDrawable(getResources(), bitmapLoading.getFloorImage(R.drawable.map_t3)));
        }
    }

    @Override
    public void getUpdatedDataSet(List<PointOfInterest> pointList) {
        RelativeLayout r = mapImage.getRelativeLayout();

        for(IndoorMapMarker p : listOfMarkers) {
            r.removeView(p.getMarker());
        }
        listOfMarkers.clear();
        for(PointOfInterest p : pointList) {
            addPoint(r,p);
        }
        floorAdapter.notifyDataSetChanged();
    }

    @Override
    public void dataSetChanged() {
        floorAdapter.notifyDataSetChanged();
    }

    //Get dimension
    private void getDimensions(final RelativeLayout relativeLayout) {
        ViewTreeObserver vto = relativeLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = relativeLayout.getViewTreeObserver();

                obs.removeOnGlobalLayoutListener(this);
                final Point point = new Point(relativeLayout.getWidth(), relativeLayout.getHeight());
                Log.d("point", "x " + point.x + " y " + point.y);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_indoor_map, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // return super.onOptionsItemSelected(item);

        int id = item.getItemId();
        if (id == R.id.floors) {
            if(getActivity().findViewById(R.id.floor_recycler_view).getVisibility() == View.GONE)
                getActivity().findViewById(R.id.floor_recycler_view).setVisibility(View.VISIBLE);
            else
                getActivity().findViewById(R.id.floor_recycler_view).setVisibility(View.GONE);
        }
        return false;
    }
}

