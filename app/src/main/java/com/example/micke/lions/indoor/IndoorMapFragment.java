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

import java.io.Serializable;
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
    private String currentFloor;
    private boolean firstLoad = true;

    //way finding
    private boolean filterMarkers;
    private IndoorMapMarker end = null, elevator = null, staircase = null, user = null;
    private PointOfInterest endPoint = new PointOfInterest();
            private String userID = "", elevatorID = "", staircaseID = "";
    private List<IndoorMapMarker> floorChange;


    private int displayWidth;
    private int displayHeight;

    //Scale test
    private MapImage mapImage;
    private BitmapLoading bitmapLoading;

    //Stores all ips for the whole building
    private List<PointOfInterest> pointList;

    private IndoorActivity indoorActivity;

    //Stores ips for the current floor as markers
    private List<IndoorMapMarker> listOfMarkers = new ArrayList<IndoorMapMarker>();

    private ImageButton goToList;
    private ImageView goHere;
    private ImageView goHere2;
    private ImageView uAreHere;
    private ImageView uAreHere2;

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
            //Log.d(TAG, "onCreateView: start floor: " + ((IndoorActivity)getActivity()).startFloor);
            setCurrentFloor(((IndoorActivity)getActivity()).startFloor);
        }

        return rootView;
    }

    //Sets up initial position of popups and the visibility is "GONE".
    //Visibility is changed to "VISIBLE" when the textViews are used.
    private void setUpPopup() {

        goHere = new ImageView(getContext());
        goHere2 = new ImageView(getContext());
        uAreHere = new ImageView(getContext());
        uAreHere2 = new ImageView(getContext());

        goHere.setAdjustViewBounds(true);
        goHere2.setAdjustViewBounds(true);
        uAreHere.setAdjustViewBounds(true);
        uAreHere2.setAdjustViewBounds(true);

        goHere.setMaxHeight(100);
        goHere.setMaxWidth(170);
        goHere.setMinimumHeight(100);
        goHere.setMinimumWidth(170);
        goHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //if the goal popup was clicked
                //end way finding
                //else switch to goal floor
                if(currentFloor.equals(endPoint.getFloor()))
                    resetView();
                else
                    changeFloor(endPoint.getFloor());
            }
        });

        goHere2.setMaxHeight(100);
        goHere2.setMaxWidth(170);
        goHere2.setMinimumHeight(100);
        goHere2.setMinimumWidth(170);
        goHere2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //if the goal popup was clicked
                //end way finding
                //else switch to goal floor
                if(currentFloor.equals(endPoint.getFloor()))
                    resetView();
                else
                    changeFloor(endPoint.getFloor());
            }
        });

        uAreHere.setMaxHeight(100);
        uAreHere.setMaxWidth(170);
        uAreHere.setMinimumHeight(100);
        uAreHere.setMinimumWidth(170);
        uAreHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //if the elevator popup was clicked
                //switch to user floor
                if(currentFloor.equals(endPoint.getFloor()))
                    changeFloor(((IndoorActivity)getActivity()).startFloor);
            }
        });

        uAreHere2.setMaxHeight(100);
        uAreHere2.setMaxWidth(170);
        uAreHere2.setMinimumHeight(100);
        uAreHere2.setMinimumWidth(170);
        uAreHere2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //if the elevator popup was clicked
                //switch to user floor
                if(currentFloor.equals(endPoint.getFloor()))
                    changeFloor(((IndoorActivity)getActivity()).startFloor);
            }
        });

        goHere.setX(0);
        goHere.setY(0);
        getRelativeLayout().addView(goHere);
        goHere.setVisibility(View.GONE);
        goHere2.setX(0);
        goHere2.setY(0);
        getRelativeLayout().addView(goHere2);
        goHere2.setVisibility(View.GONE);

        uAreHere.setX(0);
        uAreHere.setY(0);
        getRelativeLayout().addView(uAreHere);
        uAreHere.setVisibility(View.GONE);
        uAreHere2.setX(0);
        uAreHere2.setY(0);
        getRelativeLayout().addView(uAreHere2);
        uAreHere2.setVisibility(View.GONE);

        goHere.setImageResource(R.drawable.speech_bubble_go_here);
        goHere2.setImageResource(R.drawable.speech_bubble_go_here);
        uAreHere.setImageResource(R.drawable.speech_bubble_u_are_here);
        uAreHere2.setImageResource(R.drawable.speech_bubble_u_are_here);
    }

    public void showSingleIP(String floor, String ipID) {
        setCurrentFloor(floor);
        filterMarkers = true;
        user = null;

        //Find the point
        for(IndoorMapMarker m : listOfMarkers) {
            if(m.getId().equals(ipID)) {
                user = m;
            }
        }

        //Should never happen
        if(user == null) {
            Log.d(TAG, "showSingleIP: no marker found");
            return;
        }

        for (IndoorMapMarker m : listOfMarkers) {
            m.getMarker().setVisibility(View.GONE);
        }

        user.getMarker().setVisibility(View.VISIBLE);
        uAreHere.setVisibility(View.VISIBLE);
        addPopup(uAreHere, user.getX(), user.getY());
    }

    //Shows the user where to go and if the user has a position it shows that position
    //If the goal is on another floor than the user the closest elevator and staircase is shown
    public void startWayFinding(String goalFloor, String ipID) {
        resetView();
        setCurrentFloor(goalFloor);
        resetView();
        filterMarkers = true;
        floorChange = new ArrayList();
        boolean userHasPos = false;

        //Does the user have a location?
        userID = ((IndoorActivity)getActivity()).youAreHereID;
        if(!userID.equals(""))
        {
            userHasPos = true;
            //Find the user's point
            for(IndoorMapMarker m : listOfMarkers) {
                if(m.getId().equals(userID))
                    user = m;
            }
        }

        if(userID.equals(ipID)){
            //the user is at the chosen IP
            //only show user
        }
        else if(userHasPos && user == null) {
            //user and goal are on different floors

            //switch to user floor and show user + elevator or staircase
            //save the goal as a point to use later
            for(PointOfInterest p: pointList) {
                if(p.getId().equals(userID)) {
                    setCurrentFloor(p.getFloor());
                }
                else if(p.getId().equals(ipID)){
                    endPoint = p;
                }
            }

            //Find the user's point
            for(IndoorMapMarker m : listOfMarkers) {
                if(m.getId().equals(userID))
                    user = m;
            }

            //Find the closest elevator and staircase
            float distanceE = Float.MAX_VALUE;
            float distanceS = Float.MAX_VALUE;
            for (IndoorMapMarker m : listOfMarkers) {
                if (m.getCategory().equals(getString(R.string.Elevator))
                        && distanceE > calcDistance(m.getX(), m.getY(), user.getX(), user.getY())) {
                    elevator = m;
                    distanceE = calcDistance(m.getX(), m.getY(), user.getX(), user.getY());
                }
                else if (m.getCategory().equals(getString(R.string.Stairs))
                        && distanceE > calcDistance(m.getX(), m.getY(), user.getX(), user.getY())) {
                    staircase = m;
                    distanceS = calcDistance(m.getX(), m.getY(), user.getX(), user.getY());
                }
            }
        }
        else if (userHasPos) { //user != null
            //user and goal are on the same floor
            //Find the goal point
            for(IndoorMapMarker m : listOfMarkers) {
                if(m.getId().equals(ipID))
                    end = m;
            }

            //Should never happen
            if(end == null) return;
        }
        else {
            //the user has no position
            //show only the chosen IP

            //Find the goal point
            for(IndoorMapMarker m : listOfMarkers) {
                if(m.getId().equals(ipID))
                    end = m;
            }
        }

        //Hide all markers
        for(IndoorMapMarker m: listOfMarkers) {
            m.getMarker().setVisibility(View.GONE);
        }

        //Show chosen IP
        if(end != null) {
            end.getMarker().setVisibility(View.VISIBLE);
            endPoint = end.getPoint();
            goHere.setVisibility(View.VISIBLE);
            //Log.d("TAG", "X = " + end.getX() + " Y = " + end.getY());
            addPopup(goHere, end.getX(), end.getY());
        }

        //show elevator
        if(elevator != null) {
            elevator.getMarker().setVisibility(View.VISIBLE);
            elevatorID = elevator.getId();
            goHere.setVisibility(View.VISIBLE);
            addPopup(goHere, elevator.getX(), elevator.getY());
        }
        else if(end == null) {
            //user and end on different floors but no elevator found
            //can also be that user is at goal
            /*Toast toast = Toast.makeText(getContext(), "Couldn't find an elevator!", Toast.LENGTH_SHORT);
            toast.show();
            return;*/
        }

        //show staircase
        if(staircase != null) {
            staircase.getMarker().setVisibility(View.VISIBLE);
            staircaseID = staircase.getId();
            goHere2.setVisibility(View.VISIBLE);
            addPopup(goHere2, staircase.getX(), staircase.getY());
        }
        else if(end == null) {
            //user and end on different floors but no staircase found
            //can also be that user is at goal
            /*Toast toast = Toast.makeText(getContext(), "Couldn't find any stairs!", Toast.LENGTH_SHORT);
            toast.show();
            return;*/
        }

        //show user
        if(user != null) {
            user.getMarker().setVisibility(View.VISIBLE);
            uAreHere.setVisibility(View.VISIBLE);
            addPopup(uAreHere, user.getX(), user.getY());

            //if user and end are the same
            //save the end point
            if(userID.equals(ipID))
                endPoint = user.getPoint();
        }
    }

    //sets new floor and shows IP depending on wayfinding
    public void changeFloor(String floor){
        //change floor
        setCurrentFloor(floor);

        //if way finding not active
        //show all IP on this floor
        if(!filterMarkers){
            for(IndoorMapMarker m: listOfMarkers) {
                m.getMarker().setVisibility(View.VISIBLE);
            }
            return;
        }

        resetView();
        filterMarkers = true;
        //way finding active
        //display what should be shown on this floor
        for(IndoorMapMarker m: listOfMarkers) {
            m.getMarker().setVisibility(View.GONE);
            if(m.getId().equals(endPoint.getId())){
                end = m;
                end.getMarker().setVisibility(View.VISIBLE);
                goHere.setVisibility(View.VISIBLE);
                addPopup(goHere, end.getX(), end.getY());
            }
            if(m.getId().equals(userID)) {
                user = m;
                user.getMarker().setVisibility(View.VISIBLE);
                uAreHere.setVisibility(View.VISIBLE);
                addPopup(uAreHere, user.getX(), user.getY());
            }
            if(m.getId().equals(elevatorID)) {
                elevator = m;
            }
            if(m.getId().equals(staircaseID)) {
                staircase = m;
            }
        }

        //if we have an elevator and the user and goal are on different floors
        //figure out which popup it should have
        if(elevator != null && end != null && user == null)
        {
            //if the goal is on this floor and the user isn't
            //the user should go to the goal
            //and the user is at the elevator
            elevator.getMarker().setVisibility(View.VISIBLE);
            uAreHere.setVisibility(View.VISIBLE);
            addPopup(uAreHere, elevator.getX(), elevator.getY());
        }
        else if(elevator != null) {
            //the user should go to the elevator
            elevator.getMarker().setVisibility(View.VISIBLE);
            goHere.setVisibility(View.VISIBLE);
            addPopup(goHere, elevator.getX(), elevator.getY());
        }

        //if we have a staircase and the user and goal are on different floors
        //figure out which popup it should have
        if(staircase != null && end != null && user == null)
        {
            //if the goal is on this floor and the user isn't
            //the user should go to the goal
            //and the user is at the elevator
            staircase.getMarker().setVisibility(View.VISIBLE);
            uAreHere2.setVisibility(View.VISIBLE);
            addPopup(uAreHere2, staircase.getX(), staircase.getY());
        }
        else if(staircase != null) {
            //the user should go to the elevator
            staircase.getMarker().setVisibility(View.VISIBLE);
            goHere2.setVisibility(View.VISIBLE);
            addPopup(goHere2, staircase.getX(), staircase.getY());
        }
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
        final float[] point = mapImage.convertCoordinates(ip.getLatitude(), ip.getLongitude());

        final IndoorMapMarker marker = new IndoorMapMarker(ip, point[0], point[1], parent.getContext());

        parent.addView(marker.getMarker());

        listOfMarkers.add(marker);

        marker.getMarker().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!filterMarkers) {
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
                else {
                    final Toast toast = Toast.makeText(context, "Gå ur vägbeskrivning för att ändra punkt.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    public void movePoint(String id) {
        IndoorMapMarker marker;
        for(IndoorMapMarker m : listOfMarkers) {
            if(m.getId().equals(id))
                marker = m;
        }

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


    private void addPopup(ImageView imageView, float posX, float posY){

        imageView.measure(0,0);
        int x = imageView.getMeasuredWidth();
        int y = imageView.getMeasuredHeight();

        imageView.setX(posX - x/4);
        imageView.setY(posY - y - listOfMarkers.get(0).getMarker().getMaxHeight() / 2.0f);
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
            resetView();
        }
        return false;
    }

    public void resetView() {
        setCurrentFloor(fireBaseIndoor.getFloor());
        filterMarkers = false;
        end = null;
        elevator = null;
        staircase = null;
        user = null;
        uAreHere.setVisibility(View.GONE);
        uAreHere2.setVisibility(View.GONE);
        goHere.setVisibility(View.GONE);
        goHere2.setVisibility(View.GONE);
    }

    //sets the map of the floor and loads all markers into the listOfMarkers
    private void setCurrentFloor(String floor) {
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
        if(!filterMarkers) {
            uAreHere.setVisibility(View.GONE);
            goHere.setVisibility(View.GONE);

            for (IndoorMapMarker p : listOfMarkers) {
                r.removeView(p.getMarker());
            }
            listOfMarkers.clear();
            for (PointOfInterest p : pointList) {
                if (p.getFloor().equals(currentFloor) || p.getCategory().equals(getString(R.string.Stairs))
                        || p.getCategory().equals(getString(R.string.Elevator)))
                    addPoint(r, p);
            }
            floorAdapter.notifyDataSetChanged();
        }
        if (firstLoad) {
            //check if there exist a youAreHereID
            userID = ((IndoorActivity) getActivity()).youAreHereID;
            if (!userID.equals("")) {
                //the user has a position
                //is there a goal?
                String goalID = ((IndoorActivity) getActivity()).startGoalID;
                String goalFloor = ((IndoorActivity) getActivity()).startGoalFloor;
                if (!goalID.equals("") && !goalFloor.equals("")) {
                    startWayFinding(goalFloor, goalID);
                } else
                    showSingleIP(currentFloor, ((IndoorActivity) getActivity()).youAreHereID);

            }
            else
                setCurrentFloor(pointList.get(0).getFloor());
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

    public IndoorMapMarker findMarkerById(String id) {
        IndoorMapMarker res = null;
        for(IndoorMapMarker m: listOfMarkers) {
            if(m.getId().equals(id))
                res = m;
        }
        return res;
    }

    @Override
    public void adminInlogg() {
        Log.d(TAG, "adminInlogg: ");
    }

    @Override
    public void commonInlogg() {
        Log.d(TAG, "commonInlogg: ");
    }

    public PointOfInterest getGoal() {
        return endPoint;
    }

    public boolean getFilterMarkers() {
        return filterMarkers;
    }

    private RelativeLayout getRelativeLayout(){ return r;}

}

