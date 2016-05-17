package com.example.micke.lions.indoor;

import android.app.DialogFragment;

import android.content.Context;
import android.graphics.Bitmap;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    //This is how you upload a new mapimage to firebase for a specified floor:
    //fireBaseIndoor.addMap(bitmapLoading.getFloorImage(R.drawable.map_t3), 3);

    //Sorts map images by which floor they are for
    private List<FloorMapimage> images;

    //Stores all ips for the whole building
    private List<PointOfInterest> pointList;

    private IndoorActivity indoorActivity;

    //Stores ips for the current floor as markers
    private List<IndoorMapMarker> listOfMarkers = new ArrayList<IndoorMapMarker>();

    private ImageButton goToList;
    public Button nextStep;
    public Button cancel;
    public TextView goalFloorText;
    private ImageView goHere;
    private ImageView goHere2;
    private ImageView uAreHere;
    private ImageView uAreHere2;

    private static final String ARG_SECTION_NUMBER = "section_number";


    private Drawable floorMap;

    private RelativeLayout r = null;

    //Animation from VISIBLE to GONE
    private Animation animToGONE = null;

    //Animation from GONE to VISIBLE
    private Animation animToVISIBLE = null;

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
        mFloors = new ArrayList<>();
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

        animToGONE = new AlphaAnimation(1.0f, 0.0f);
        animToGONE.setDuration(200);

        animToVISIBLE = new AlphaAnimation(0.0f, 1.0f);
        animToVISIBLE.setDuration(200);

        //For the map
        r = (RelativeLayout) rootView.findViewById(R.id.mapLayout);
        r.setScaleX(1.0f);
        r.setScaleY(1.0f);

        //Sets popup properties.
        setUpPopup();

        //Get dimensions of r
        Log.d("point", "getting dimensions...");
        getDimensions(r);

        mapImage = (MapImage) rootView.findViewById(R.id.scale_test);
        mapImage.setParent(r);
        mapImage.setCallback(this);
        mapImage.setImage(new BitmapDrawable(getResources(), bitmapLoading.getFloorImage(R.drawable.loading)));
        setHasOptionsMenu(true);

        //Don't load points until map images are loaded
        pointList = new ArrayList<>();
        images = fireBaseIndoor.getMapimages(buildingId, this);

        goToList = (ImageButton) rootView.findViewById(R.id.goToIndoorList1);
        goToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager mPager = (ViewPager) v.getRootView().findViewById(R.id.container);
                mPager.setCurrentItem(1, true);
            }
        });

        cancel = (Button) rootView.findViewById(R.id.cancel_way);
        cancel.setVisibility(View.GONE);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopWayFinding();
            }
        });

        nextStep = (Button) rootView.findViewById(R.id.next);
        nextStep.setVisibility(View.GONE);
        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(endPoint.getFloor().equals(currentFloor))
                    stopWayFinding();
                else
                    changeFloor(endPoint.getFloor());
            }
        });

        goalFloorText = (TextView) rootView.findViewById(R.id.goal_floor_text);
        goalFloorText.setVisibility(View.GONE);

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
        cancel.setVisibility(View.VISIBLE);
        nextStep.setVisibility(View.VISIBLE);
        nextStep.setText("Gå vidare"); //text changes later if we show goal floor
        goalFloorText.setVisibility(View.VISIBLE);
        goalFloorText.setText("Målet är på våning " + currentFloor);
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

            //if the goal is shown the next button should say done
            nextStep.setText("Klar");
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
            /*Toast toast = Toast.makeText(getContext(), "Couldn't find an elevator!", Toast.LENGTH_LONG);
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
            /*Toast toast = Toast.makeText(getContext(), "Couldn't find any stairs!", Toast.LENGTH_LONG);
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

    //sets new floor and shows IP depending on way finding
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
        cancel.setVisibility(View.VISIBLE);
        nextStep.setVisibility(View.VISIBLE);
        nextStep.setText("Gå vidare");
        goalFloorText.setVisibility(View.VISIBLE);
        goalFloorText.setText("Målet är på våning " + currentFloor);
        //way finding active
        //display what should be shown on this floor
        for(IndoorMapMarker m: listOfMarkers) {
            m.getMarker().setVisibility(View.GONE);
            if(m.getId().equals(endPoint.getId())){
                end = m;
                end.getMarker().setVisibility(View.VISIBLE);
                goHere.setVisibility(View.VISIBLE);
                addPopup(goHere, end.getX(), end.getY());
                //if end is on this floor the next button should say done
                nextStep.setText("Klar");
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
        //TODO
        final float[] point = mapImage.convertCoordinates(ip.getLatitude(), ip.getLongitude());
        final IndoorMapMarker marker = new IndoorMapMarker(ip, point[0], point[1], parent.getContext());

        parent.addView(marker.getMarker());

        listOfMarkers.add(marker);

        marker.getMarker().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Checks user have chosen an IP
                if(filterMarkers) {
                    //FULASTE JÄÄVLA LÖSNINGEN I GALAXEN???

                    //If user have scanned a QR code
                    if(user != null) {
                        if (marker.getId() == user.getId() && uAreHere.getVisibility() == View.VISIBLE) {
                            uAreHere.startAnimation(animToGONE);
                            uAreHere.setVisibility(View.GONE);
                        } else if (marker.getId() == user.getId() && uAreHere.getVisibility() == View.GONE) {
                            uAreHere.startAnimation(animToVISIBLE);
                            uAreHere.setVisibility(View.VISIBLE);
                        }
                    }
                    //If user have scanned a QR code and clicked on an IP for wayfinding
                    if(end != null) {
                        if (marker.getId() == end.getId() && goHere.getVisibility() == View.VISIBLE) {
                            goHere.startAnimation(animToGONE);
                            goHere.setVisibility(View.GONE);
                        } else if (marker.getId() == end.getId() && goHere.getVisibility() == View.GONE) {
                            goHere.startAnimation(animToVISIBLE);
                            goHere.setVisibility(View.VISIBLE);
                        }
                    }

                    //If user have clicked on QR code and IP and haven´t changed floor
                    if(elevator != null && user != null) {
                        if (marker.getId() == elevator.getId() && goHere.getVisibility() == View.VISIBLE) {
                            goHere.startAnimation(animToGONE);
                            goHere.setVisibility(View.GONE);
                        } else if (marker.getId() == elevator.getId() && goHere.getVisibility() == View.GONE) {
                            goHere.startAnimation(animToVISIBLE);
                            goHere.setVisibility(View.VISIBLE);
                        }
                    }
                    //If user have clicked on QR code and IP and have changed floor
                    if(elevator != null && user == null) {
                        if (marker.getId() == elevator.getId() && uAreHere.getVisibility() == View.VISIBLE) {
                            uAreHere.startAnimation(animToGONE);
                            uAreHere.setVisibility(View.GONE);
                        } else if (marker.getId() == elevator.getId() && uAreHere.getVisibility() == View.GONE) {
                            uAreHere.startAnimation(animToVISIBLE);
                            uAreHere.setVisibility(View.VISIBLE);
                        }
                    }
                    //If user have clicked on QR code and IP and haven´t changed floor
                    if(staircase != null && user != null){
                        if(marker.getId() == staircase.getId() && goHere2.getVisibility() == View.VISIBLE){
                            goHere2.startAnimation(animToGONE);
                            goHere2.setVisibility(View.GONE);
                        } else if (marker.getId() == staircase.getId() && goHere2.getVisibility() == View.GONE) {
                            goHere2.startAnimation(animToVISIBLE);
                            goHere2.setVisibility(View.VISIBLE);
                        }
                    }
                    //If user have clicked on QR code and IP and have changed floor
                    if(staircase != null && user == null){
                        if(marker.getId() == staircase.getId() && uAreHere2.getVisibility() == View.VISIBLE){
                            uAreHere2.startAnimation(animToGONE);
                            uAreHere2.setVisibility(View.GONE);
                        } else if (marker.getId() == staircase.getId() && uAreHere2.getVisibility() == View.GONE) {
                            uAreHere2.startAnimation(animToVISIBLE);
                            uAreHere2.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

        marker.getMarker().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
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
                    final Toast toast = Toast.makeText(context, "Gå ur vägbeskrivning för att ändra punkt.", Toast.LENGTH_LONG);
                    toast.show();
                }
                return false;
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
            floorAdapter.setData(mFloors); //add updates here
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
        cancel.setVisibility(View.GONE);
        nextStep.setVisibility(View.GONE);
        goalFloorText.setVisibility(View.GONE);
    }

    public void stopWayFinding() {
        resetView();
        endPoint = new PointOfInterest();
        userID = "";
        elevatorID = "";
        staircaseID = "";
    }

    //Only called by mapImage's init() method. Adds points to the image after the image is loaded
    public void fillFloorWithPoints() {
        RelativeLayout r = mapImage.getRelativeLayout();
        for (IndoorMapMarker p : listOfMarkers)
            r.removeView(p.getMarker());
        listOfMarkers.clear();
        for (PointOfInterest p : pointList) {
            if (p.getFloor().equals(currentFloor) || p.getCategory().equals(getString(R.string.Stairs))
                    || p.getCategory().equals(getString(R.string.Elevator))) {
                addPoint(r, p);
            }
        }
    }
    //sets the map of the floor and loads all markers into the listOfMarkers
    private void setCurrentFloor(String floor) {
        fireBaseIndoor.setFloor(floor);
        currentFloor = floor;
        for(FloorMapimage i : images) {
            if(floor.equals(Integer.toString(i.floor))) {
                //This will indirectly call fillFloorWithPoints() above once image is done loading
                mapImage.setImage(new BitmapDrawable(getResources(), i.mapimage));
            }
        }
        mapImage.resetView();
    }

    //This function takes care of map initialization for the mapimage and markers
    @Override
    public void getMapimagesDataSet(List<FloorMapimage> mapimageList) {
        //Start loading points
        pointList = fireBaseIndoor.getPoints(buildingId, this);

        images = mapimageList;

        for(FloorMapimage fmi : mapimageList) {
            mFloors.add(""+fmi.floor);
        }

        if(!indoorActivity.startFloor.equals("")){
            changeFloor(indoorActivity.startFloor);
        }
        //Standard floor is 3 because reasons.
        else changeFloor("3");
    }

    @Override
    public void getUpdatedDataSet(List<PointOfInterest> pointList) {
        Log.d(TAG, "getUpdatedDataSet: filter = " + filterMarkers);
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
            firstLoad = false;
        }
        else {
            if(filterMarkers) {
                startWayFinding(endPoint.getFloor(), endPoint.getId());
            }
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

