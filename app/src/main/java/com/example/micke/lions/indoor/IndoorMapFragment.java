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
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.micke.lions.Common;
import com.example.micke.lions.InloggChange;
import com.example.micke.lions.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class IndoorMapFragment extends Fragment implements IndoorMapMarkerChange, InloggChange {
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
    private Context context;
    private String currentFloor = "3"; //TODO

    private int displayWidth;
    private int displayHeight;

    //Scaletest
    private MapImage mapImage;
    private BitmapLoading bitmapLoading;

    private List<PointOfInterest> pointList;

    private IndoorActivity indoorActivity;
    private List<IndoorMapMarker> listOfMarkers = new ArrayList<IndoorMapMarker>();

    private ImageButton goToList;

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

//        goToList = (ImageButton) rootView.findViewById(R.id.goToIndoorList1);
//        goToList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ViewPager mPager = (ViewPager) v.getRootView().findViewById(R.id.container);
//                mPager.setCurrentItem(1, true);
//            }
//        });

        setCurrentFloor("3");

        return rootView;
    }

    public void highlightIP(String goalFloor, String ipID) {
        setCurrentFloor(goalFloor);
        //hide all except chosen ip and entrance (or stairs/elevator)
        IndoorMapMarker start = null, end = null;

        //Find the goal point
        for(IndoorMapMarker m : listOfMarkers) {
            if(m.getId().equals(ipID))
                end = m;
        }

        Log.d("hejhej", "size = " + listOfMarkers.size());
        //Find the closest entrance (if it exist on this floor)
        float distance = 1000000;
        for(IndoorMapMarker m : listOfMarkers) {
            if (m.getCategory().equals(getString(R.string.Entrance)))
            Log.d("hejhej", "entre: " + m.getPoint().getTitle() + ". Distance = "  + distance + " > " + calcDistance(m.getX(), m.getY(), end.getX(), end.getY()));
            if (m.getCategory().equals(getString(R.string.Entrance)) && distance > calcDistance(m.getX(), m.getY(), end.getX(), end.getY())) {
                start = m;
                distance = calcDistance(m.getX(), end.getX(), m.getY(), end.getY());
            }
        }

        //If we didn't find an entrance we look for the closest elevator or stairs instead
        if(start == null) {
            for (IndoorMapMarker m : listOfMarkers) {
                if ( (m.getCategory().equals(getString(R.string.Elevator)) || m.getCategory().equals(getString(R.string.Stairs)))
                            && distance > calcDistance(m.getX(), m.getY(), end.getX(), end.getY()) ) {
                    start = m;
                    distance = calcDistance(m.getX(), m.getY(), end.getX(), end.getY());
                }
            }
        }

        for (IndoorMapMarker m : listOfMarkers) {
            m.getMarker().setVisibility(View.GONE);
        }
        start.getMarker().setVisibility(View.VISIBLE);
        end.getMarker().setVisibility(View.VISIBLE);
    }

    //Calculates the distance between two points
    private float calcDistance(float point1X, float point1Y, float point2X, float point2Y) {
        return (float) Math.sqrt( Math.pow((point1X-point2X), 2) + Math.pow((point1Y-point2Y), 2) );
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
//            addDescText(parent, ip.getCategory(), point.getX(), point.getY());

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

    //Before merge
    public void setCurrentFloor(String floor) {
        Log.d("floor", "" + floor);
        mapImage.resetView();

        fireBaseIndoor.setFloor(floor);
        currentFloor = floor;

        if (floor.equals("3")) {
            mapImage.setImage(new BitmapDrawable(getResources(), bitmapLoading.getFloorImage(R.drawable.map_t3)));
        }
        if (floor.equals("4")) {
            mapImage.setImage(new BitmapDrawable(getResources(), bitmapLoading.getFloorImage(R.drawable.map_t3)));
        }
        mapImage.resetView();

//        RelativeLayout r = (RelativeLayout) rootView.findViewById(R.id.mapLayout);
//        for(IndoorMapMarker p : listOfMarkers)
//            r.removeView(p.getMarker());
//        listOfMarkers.clear();
//        for(PointOfInterest p : pointList) {
//            if(p.getFloor().equals(floor))
//                addPoint(r,p);
//        }
    }

    //From master
//    public void setCurrentFloor(String floor) {
//        ImageView i = (ImageView) rootView.findViewById(R.id.map);
//
//        Log.d("floor", "" + floor + " pointList size = " + pointList.size());
//        fireBaseIndoor.setFloor(floor);
//        floorMap = null;
//        currentFloor = floor;
//        if(floor.equals("3")) {
//            floorMap = getResources().getDrawable(R.drawable.map_t3);
//            floorMap = new BitmapDrawable(getResources(), getFloorImage(R.drawable.map_t3));
//        }
//        if(floor.equals("4")) {
//            floorMap = getResources().getDrawable(R.drawable.map_t4);
//            floorMap = new BitmapDrawable(getResources(), getFloorImage(R.drawable.map_t4));
//        }
//        i.setImageDrawable(floorMap);
//
//        RelativeLayout r = (RelativeLayout) rootView.findViewById(R.id.mapLayout);
//        for(IndoorMapMarker p : listOfMarkers)
//            r.removeView(p.getMarker());
//        listOfMarkers.clear();
//        for(PointOfInterest p : pointList) {
//            if(p.getFloor().equals(floor))
//                addPoint(r,p);
//        }
//    }

    @Override
    public void getUpdatedDataSet(List<PointOfInterest> pointList) {
        RelativeLayout r = mapImage.getRelativeLayout();

        for(IndoorMapMarker p : listOfMarkers) {
            r.removeView(p.getMarker());
        }
        listOfMarkers.clear();
        for(PointOfInterest p : pointList) {
            if(p.getFloor().equals(currentFloor))
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

    @Override
    public void adminInlogg() {
        Log.d(TAG, "adminInlogg: ");
    }

    @Override
    public void commonInlogg() {
        Log.d(TAG, "commonInlogg: ");
    }
}

