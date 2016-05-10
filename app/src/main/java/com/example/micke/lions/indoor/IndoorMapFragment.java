package com.example.micke.lions.indoor;

import android.app.DialogFragment;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import android.view.WindowManager;
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
import java.util.concurrent.locks.ReadWriteLock;

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
    private boolean firstLoad = true;

    private boolean filterMarkers;
    private IndoorMapMarker end = null, entrance = null;
    private List<IndoorMapMarker> floorChange;


    private int displayWidth;
    private int displayHeight;

    //Scaletest
    private MapImage mapImage;
    private BitmapLoading bitmapLoading;

    //Stores all ips for the whole building
    private List<PointOfInterest> pointList;

    private IndoorActivity indoorActivity;

    //Stores ips for the current floor as markers
    private List<IndoorMapMarker> listOfMarkers = new ArrayList<IndoorMapMarker>();

    private ImageButton goToList;
    private ImageView goHere;
    private ImageView uAreHere;

    private static final String ARG_SECTION_NUMBER = "section_number";


    private Drawable floorMap;

    private RelativeLayout r = null;

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
        filterMarkers = false;
        indoorActivity = (IndoorActivity) getActivity();
        fireBaseIndoor = indoorActivity.getFireBaseHandler();
        context = getContext();

        buildingId = indoorActivity.getBuildingId();
        rootView = inflater.inflate(R.layout.fragment_indoor_map, container, false);
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
        r = (RelativeLayout) rootView.findViewById(R.id.mapLayout);
        r.setScaleX(1.0f);
        r.setScaleY(1.0f);

        //Sets popup properties.
        setUpPopup();

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

        goToList = (ImageButton) rootView.findViewById(R.id.goToIndoorList1);
        goToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager mPager = (ViewPager) v.getRootView().findViewById(R.id.container);
                mPager.setCurrentItem(1, true);
            }
        });


        if(!((IndoorActivity)getActivity()).startFloor.equals("")){
            Log.d(TAG, "onCreateView: start floor: " + ((IndoorActivity)getActivity()).startFloor);
            setCurrentFloor(((IndoorActivity)getActivity()).startFloor);
        }
        else {
            Log.d(TAG, "onCreateView: no start floor");
            setCurrentFloor("3");
        }

        return rootView;
    }

    //Sets up initial position of popups and the visibility is "GONE".
    //Visibility is changed to "VISIBLE" when the textViews are used.
    private void setUpPopup() {

        goHere = new ImageView(getContext());
        uAreHere = new ImageView(getContext());

        goHere.setAdjustViewBounds(true);
        uAreHere.setAdjustViewBounds(true);

        goHere.setMaxHeight(100);
        goHere.setMaxWidth(170);
        goHere.setMinimumHeight(100);
        goHere.setMinimumWidth(170);

        uAreHere.setMaxHeight(100);
        uAreHere.setMaxWidth(170);
        uAreHere.setMinimumHeight(100);
        uAreHere.setMinimumWidth(170);

        goHere.setX(0);
        goHere.setY(0);
        getRelativeLayout().addView(goHere);
        goHere.setVisibility(View.GONE);

        uAreHere.setX(0);
        uAreHere.setY(0);
        getRelativeLayout().addView(uAreHere);
        uAreHere.setVisibility(View.GONE);

        goHere.setImageResource(R.drawable.speech_bubble_go_here);
        uAreHere.setImageResource(R.drawable.speech_bubble_u_are_here);

    }

    public void showSingleIP(String floor, String ipID) {
        setCurrentFloor(floor);
        filterMarkers = true;
        entrance = null;

        //Find the point
        for(IndoorMapMarker m : listOfMarkers) {
            if(m.getId().equals(ipID)) {
                Log.d(TAG, "showSingleIP: found marker");
                entrance = m;
            }
        }

        //Should never happen
        if(entrance == null) {
            Log.d(TAG, "showSingleIP: no marker found");
            return;
        }

        for (IndoorMapMarker m : listOfMarkers) {
            m.getMarker().setVisibility(View.GONE);
            Log.d(TAG, "showSingleIP: gone");
        }

        entrance.getMarker().setVisibility(View.VISIBLE);
        Log.d(TAG, "showSingleIP: visible");
    }

    //should maybe be called leadTheWay or something so that highlight can be used for a single IP
    //hide all except chosen ip and entrance (or stairs/elevator)
    public void highlightIP(String goalFloor, String ipID) {
        setCurrentFloor(goalFloor);
        filterMarkers = true;
        entrance = null;
        end = null;
        floorChange = new ArrayList();

        //Find the goal point
        for(IndoorMapMarker m : listOfMarkers) {
            if(m.getId().equals(ipID))
                end = m;
        }

        //Should never happen
        if(end == null) return;

        if(!end.getCategory().equals(getString(R.string.Elevator)) && !end.getCategory().equals(getString(R.string.Stairs))
                && !end.getCategory().equals(getString(R.string.Entrance))) {
            //Find the closest entrance (if it exist on this floor)
            float distance = Float.MAX_VALUE;
            for (IndoorMapMarker m : listOfMarkers) {
                if (m.getCategory().equals(getString(R.string.Entrance)) && distance > calcDistance(m.getX(), m.getY(), end.getX(), end.getY())) {
                    entrance = m;
                    distance = calcDistance(m.getX(), m.getY(), end.getX(), end.getY());
                }
            }

            //If we didn't find an entrance we look for the closest elevator or stairs instead
            if (entrance == null) {
                for (IndoorMapMarker m : listOfMarkers) {
                    if ((m.getCategory().equals(getString(R.string.Elevator)) || m.getCategory().equals(getString(R.string.Stairs)))
                            && distance > calcDistance(m.getX(), m.getY(), end.getX(), end.getY())) {
                        entrance = m;
                        distance = calcDistance(m.getX(), m.getY(), end.getX(), end.getY());
                    }
                }
            }
        } else entrance = end;

        for (IndoorMapMarker m : listOfMarkers) {
            m.getMarker().setVisibility(View.GONE);
        }

        end.getMarker().setVisibility(View.VISIBLE);
        uAreHere.setVisibility(View.VISIBLE);
        Log.d("TAG", "X = " + end.getX() + " Y = " + end.getY());
        addPopup(uAreHere, end.getX(), end.getY());

        //Return if we didn't find an elevator/stairs or entrance
        if(entrance == null) {
            Toast toast = Toast.makeText(getContext(), "Couldn't find an entrance, elevator or stairs!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        entrance.getMarker().setVisibility(View.VISIBLE);
        if(entrance != end)
            goHere.setVisibility(View.VISIBLE);
            addPopup(goHere, entrance.getX(), entrance.getY());
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

        final IndoorMapMarker marker = new IndoorMapMarker(ip, point[0], point[1], parent.getContext());

        parent.addView(marker.getMarker());

        listOfMarkers.add(marker);

        marker.getMarker().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (marker.getOfficial() && !Common.IsAdmin()) {
                    Toast toast = Toast.makeText(getContext(), R.string.adminRequierdForChangingPoint, Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    ChangePointDialogFragment ask = new ChangePointDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("id", marker.getId());
                    bundle.putString("category", marker.getCategory());
                    bundle.putString("title", marker.getTitle());
                    bundle.putString("description", marker.getDescription());
                    bundle.putFloat("lat", marker.getPoint().getLatitude());
                    bundle.putFloat("lng", marker.getPoint().getLongitude());
                    ask.setArguments(bundle);
                    ask.show(indoorActivity.getFragmentManager(), "remove_point_fragment");
                }
            }
        });
    }

    public void RemovePoint(String pointId)
    {
        IndoorMapMarker point = null;
        for (IndoorMapMarker p : listOfMarkers){
            if(p.getId().equals(pointId)){
                point = p;
                break;
            }
        }
        if (point!=null)
        {
            //remove from map
            listOfMarkers.remove(point);
            pointList.remove(point.getPoint());
            RelativeLayout r = (RelativeLayout) rootView.findViewById(R.id.mapLayout);
            r.removeView(point.getMarker());

            //remove from fierbase
            fireBaseIndoor.removeIp(point.getPoint());
            Log.d(TAG, "Punkt ska vara borta ");
        }
    }


    private void addPopup(ImageView textView, float posX, float posY){

        textView.measure(0,0);
        int x = textView.getMeasuredWidth();
        int y = textView.getMeasuredHeight();

        Log.d(TAG, "addPopup: x = " + x + ", y = " + y);

        textView.setX(posX - x/4);
        textView.setY(posY - y - 20); // TODO 20 is a magic number that is taken from half the size of the marker
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_indoor_map, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.floors) {
            if(getActivity().findViewById(R.id.floor_recycler_view).getVisibility() == View.GONE)
                getActivity().findViewById(R.id.floor_recycler_view).setVisibility(View.VISIBLE);
            else
                getActivity().findViewById(R.id.floor_recycler_view).setVisibility(View.GONE);
        }
        else if (id == R.id.addInterestPoint) {
            Context context = getContext();
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, R.string.addMarkerExplanation, duration);
            toast.show();
            toast.setGravity(Gravity.TOP| Gravity.CENTER, 0, 150);

        }
        else if(id == R.id.restorePoints) {
            setCurrentFloor(fireBaseIndoor.getFloor());
            filterMarkers = false;
            end = null;
            entrance = null;
        }
        return false;
    }

    //sets the map of the floor and loads all markers into the listOfMarkers
    public void setCurrentFloor(String floor) {
        Log.d("floor", "" + floor);
        mapImage.resetView();
        fireBaseIndoor.setFloor(floor);
        currentFloor = floor;

        if (floor.equals("3")) {
            mapImage.setImage(new BitmapDrawable(getResources(), bitmapLoading.getFloorImage(R.drawable.map_t3)));
        } else if (floor.equals("4")) {
            mapImage.setImage(new BitmapDrawable(getResources(), bitmapLoading.getFloorImage(R.drawable.map_t4)));
        }

        mapImage.resetView();

//        RelativeLayout r = (RelativeLayout) rootView.findViewById(R.id.mapLayout);
        RelativeLayout r = mapImage.getRelativeLayout();
        for (IndoorMapMarker p : listOfMarkers)
            r.removeView(p.getMarker());
        listOfMarkers.clear();
        for (PointOfInterest p : pointList) {
            if (p.getFloor().equals(floor) || p.getCategory().equals(getString(R.string.Stairs))
                    || p.getCategory().equals(getString(R.string.Elevator))) {
                addPoint(r, p);
            }
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
            if(p.getFloor().equals(currentFloor) || p.getCategory().equals(getString(R.string.Stairs))
                    || p.getCategory().equals(getString(R.string.Elevator)))
                addPoint(r,p);
        }
        floorAdapter.notifyDataSetChanged();
        if(firstLoad) {
            //check if there exist a youAreHereID
            if(!((IndoorActivity)getActivity()).youAreHereID.equals("")){
                showSingleIP(currentFloor, ((IndoorActivity)getActivity()).youAreHereID);
            }
            firstLoad = false;
        }
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
    public void adminInlogg() {
        Log.d(TAG, "adminInlogg: ");
    }

    @Override
    public void commonInlogg() {
        Log.d(TAG, "commonInlogg: ");
    }

    public IndoorMapMarker getEnd() {
        return end;
    }

    public boolean getFilterMarkers() {
        return filterMarkers;
    }

    private RelativeLayout getRelativeLayout(){ return r;}

}

